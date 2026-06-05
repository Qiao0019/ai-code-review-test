package com.test.pitfalls;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class RetryUtils {
    
    public static <T> T executeWithRetry(Callable<T> task, int maxRetries) {
        return executeWithRetry(task, maxRetries, 1000, TimeUnit.MILLISECONDS);
    }
    
    public static <T> T executeWithRetry(Callable<T> task, int maxRetries, long delay, TimeUnit unit) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries must be non-negative");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts <= maxRetries) {
            try {
                return task.call();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                if (attempts <= maxRetries) {
                    try {
                        unit.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("Failed after " + maxRetries + " retries", lastException);
    }
    
    public static void executeWithRetry(Runnable task, int maxRetries) {
        executeWithRetry(() -> {
            task.run();
            return null;
        }, maxRetries);
    }
    
    public static <T> T executeWithRetry(Callable<T> task, int maxRetries, BackoffStrategy backoff) {
        if (task == null || backoff == null) {
            throw new IllegalArgumentException("Task and backoff strategy cannot be null");
        }
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts <= maxRetries) {
            try {
                return task.call();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                if (attempts <= maxRetries) {
                    try {
                        Thread.sleep(backoff.getDelay(attempts));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("Failed after " + maxRetries + " retries", lastException);
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
    
    public static BackoffStrategy fibonacciBackoff(long initialDelayMs) {
        return attempt -> {
            if (attempt <= 1) return initialDelayMs;
            long a = initialDelayMs, b = initialDelayMs;
            for (int i = 2; i < attempt; i++) {
                long c = a + b;
                a = b;
                b = c;
            }
            return b;
        };
    }
    
    public static BackoffStrategy boundedBackoff(BackoffStrategy delegate, long maxDelayMs) {
        return attempt -> Math.min(delegate.getDelay(attempt), maxDelayMs);
    }
    
    public static <T> T executeWithRetry(Callable<T> task, int maxRetries, BackoffStrategy backoff, Predicate<Exception> retryPredicate) {
        if (task == null || backoff == null) {
            throw new IllegalArgumentException("Task and backoff strategy cannot be null");
        }
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts <= maxRetries) {
            try {
                return task.call();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                if (attempts <= maxRetries && (retryPredicate == null || retryPredicate.test(e))) {
                    try {
                        Thread.sleep(backoff.getDelay(attempts));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                } else {
                    break;
                }
            }
        }
        
        throw new RuntimeException("Failed after " + maxRetries + " retries", lastException);
    }
    
    public static Predicate<Exception> retryOnNetworkErrors() {
        return e -> {
            String className = e.getClass().getName();
            return className.contains("ConnectException") || 
                   className.contains("SocketTimeoutException") || 
                   className.contains("UnknownHostException");
        };
    }
}