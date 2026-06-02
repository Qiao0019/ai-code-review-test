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
}