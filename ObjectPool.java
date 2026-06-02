package com.test.pitfalls;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class ObjectPool<T> {
    
    private final Queue<T> pool;
    private final Supplier<T> creator;
    private final int maxSize;
    
    public ObjectPool(Supplier<T> creator, int initialSize, int maxSize) {
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        if (initialSize < 0) {
            throw new IllegalArgumentException("Initial size must be non-negative");
        }
        if (maxSize < initialSize) {
            throw new IllegalArgumentException("Max size must be >= initial size");
        }
        
        this.creator = creator;
        this.maxSize = maxSize;
        this.pool = new ConcurrentLinkedQueue<>();
        
        for (int i = 0; i < initialSize; i++) {
            pool.add(creator.get());
        }
    }
    
    public T acquire() {
        T obj = pool.poll();
        if (obj != null) {
            return obj;
        }
        
        synchronized (this) {
            if (pool.size() < maxSize) {
                return creator.get();
            }
        }
        
        return null;
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
}