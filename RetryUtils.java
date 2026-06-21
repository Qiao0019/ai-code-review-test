package com.test.pitfalls;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class RetryUtils {
    
    public static <T> T executeWithRetry(Callable<T> task, RetryConfig config) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (config == null) {
            config = RetryConfig.defaultConfig();
        }
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts <= config.getMaxRetries()) {
            try {
                return task.call();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                if (attempts <= config.getMaxRetries() && 
                    (config.getRetryPredicate() == null || config.getRetryPredicate().test(e))) {
                    try {
                        long delay = config.getBackoffStrategy().getDelay(attempts);
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RetryInterruptedException("Retry interrupted", ie, attempts);
                    }
                } else {
                    break;
                }
            }
        }
        
        throw new RetryExhaustedException("Failed after " + attempts + " attempts", lastException, attempts);
    }
    
    public static class RetryConfig {
        private final int maxRetries;
        private final BackoffStrategy backoffStrategy;
        private final Predicate<Exception> retryPredicate;
        
        public RetryConfig(int maxRetries, BackoffStrategy backoffStrategy, Predicate<Exception> retryPredicate) {
            this.maxRetries = maxRetries;
            this.backoffStrategy = backoffStrategy;
            this.retryPredicate = retryPredicate;
        }
        
        public static RetryConfig defaultConfig() {
            return new RetryConfig(3, fixedBackoff(1000), null);
        }
        
        public static RetryConfigBuilder builder() {
            return new RetryConfigBuilder();
        }
        
        public int getMaxRetries() {
            return maxRetries;
        }
        
        public BackoffStrategy getBackoffStrategy() {
            return backoffStrategy;
        }
        
        public Predicate<Exception> getRetryPredicate() {
            return retryPredicate;
        }
        
        public static class RetryConfigBuilder {
            private int maxRetries = 3;
            private BackoffStrategy backoffStrategy = fixedBackoff(1000);
            private Predicate<Exception> retryPredicate = null;
            
            public RetryConfigBuilder maxRetries(int maxRetries) {
                this.maxRetries = maxRetries;
                return this;
            }
            
            public RetryConfigBuilder backoff(BackoffStrategy backoffStrategy) {
                this.backoffStrategy = backoffStrategy;
                return this;
            }
            
            public RetryConfigBuilder retryOn(Predicate<Exception> retryPredicate) {
                this.retryPredicate = retryPredicate;
                return this;
            }
            
            public RetryConfig build() {
                return new RetryConfig(maxRetries, backoffStrategy, retryPredicate);
            }
        }
    }
    
    public interface BackoffStrategy {
        long getDelay(int attempt);
    }
    
    public static BackoffStrategy fixedBackoff(long delayMs) {
        return attempt -> delayMs;
    }
    
    public static BackoffStrategy exponentialBackoff(long initialDelayMs, double multiplier) {
        return attempt -> (long) (initialDelayMs * Math.pow(multiplier, attempt - 1));
    }
    
    public static BackoffStrategy linearBackoff(long initialDelayMs, long incrementMs) {
        return attempt -> initialDelayMs + (attempt - 1) * incrementMs;
    }
    
    public static class RetryInterruptedException extends RuntimeException {
        private final int attempts;
        
        public RetryInterruptedException(String message, Throwable cause, int attempts) {
            super(message, cause);
            this.attempts = attempts;
        }
        
        public int getAttempts() {
            return attempts;
        }
    }
    
    public static class RetryExhaustedException extends RuntimeException {
        private final int attempts;
        
        public RetryExhaustedException(String message, Throwable cause, int attempts) {
            super(message, cause);
            this.attempts = attempts;
        }
        
        public int getAttempts() {
            return attempts;
        }
    }
}