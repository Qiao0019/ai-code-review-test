package com.test.pitfalls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ObjectPool<T> {
    
    private final BlockingQueue<T> pool;
    private final Supplier<T> creator;
    private final int maxSize;
    private final PoolConfig config;
    
    public ObjectPool(Supplier<T> creator, PoolConfig config) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        if (config == null) {
            config = PoolConfig.defaultConfig();
        }
        
        this.creator = creator;
        this.maxSize = config.getMaxSize();
        this.config = config;
        this.pool = new LinkedBlockingQueue<>(maxSize);
        
        for (int i = 0; i < config.getInitialSize(); i++) {
            pool.offer(creator.get());
        }
    }
    
    public static class PoolConfig {
        private final int initialSize;
        private final int maxSize;
        private final long acquireTimeoutMs;
        private final boolean validateOnRelease;
        
        public PoolConfig(int initialSize, int maxSize, long acquireTimeoutMs, boolean validateOnRelease) {
            this.initialSize = initialSize;
            this.maxSize = maxSize;
            this.acquireTimeoutMs = acquireTimeoutMs;
            this.validateOnRelease = validateOnRelease;
        }
        
        public static PoolConfig defaultConfig() {
            return new PoolConfig(5, 20, 5000, false);
        }
        
        public static PoolConfigBuilder builder() {
            return new PoolConfigBuilder();
        }
        
        public int getInitialSize() {
            return initialSize;
        }
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public long getAcquireTimeoutMs() {
            return acquireTimeoutMs;
        }
        
        public boolean isValidateOnRelease() {
            return validateOnRelease;
        }
        
        public static class PoolConfigBuilder {
            private int initialSize = 5;
            private int maxSize = 20;
            private long acquireTimeoutMs = 5000;
            private boolean validateOnRelease = false;
            
            public PoolConfigBuilder initialSize(int initialSize) {
                this.initialSize = initialSize;
                return this;
            }
            
            public PoolConfigBuilder maxSize(int maxSize) {
                this.maxSize = maxSize;
                return this;
            }
            
            public PoolConfigBuilder acquireTimeout(long timeoutMs) {
                this.acquireTimeoutMs = timeoutMs;
                return this;
            }
            
            public PoolConfigBuilder validateOnRelease(boolean validate) {
                this.validateOnRelease = validate;
                return this;
            }
            
            public PoolConfig build() {
                return new PoolConfig(initialSize, maxSize, acquireTimeoutMs, validateOnRelease);
            }
        }
    }
    
    public T acquire() {
        T obj = pool.poll();
        if (obj != null) {
            return obj;
        }
        
        if (pool.size() < maxSize) {
            return creator.get();
        }
        
        return null;
    }
    
    public T acquireWithTimeout() throws InterruptedException {
        return pool.poll(config.getAcquireTimeoutMs(), TimeUnit.MILLISECONDS);
    }
    
    public void release(T obj) {
        if (obj != null && pool.size() < maxSize) {
            pool.offer(obj);
        }
    }
    
    public int getCurrentSize() {
        return pool.size();
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public void clear() {
        pool.clear();
    }
    
    public boolean isEmpty() {
        return pool.isEmpty();
    }
    
    public boolean isFull() {
        return pool.size() >= maxSize;
    }
    
    public double getUtilization() {
        return (double) pool.size() / maxSize * 100;
    }
}