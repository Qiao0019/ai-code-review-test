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
    
    public T acquire(long timeout, TimeUnit unit) throws InterruptedException {
        T obj = pool.poll();
        if (obj != null) {
            return obj;
        }
        
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        while (System.currentTimeMillis() < deadline) {
            synchronized (this) {
                if (pool.size() < maxSize) {
                    return creator.get();
                }
            }
            obj = pool.poll(10, TimeUnit.MILLISECONDS);
            if (obj != null) {
                return obj;
            }
        }
        
        return null;
    }
    
    public List<T> acquireBulk(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            T obj = acquire();
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }
    
    public void releaseBulk(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        for (T obj : objects) {
            release(obj);
        }
    }
    
    public void refill(int targetSize) {
        if (targetSize <= 0) {
            return;
        }
        int currentSize = pool.size();
        int toAdd = Math.max(0, Math.min(targetSize - currentSize, maxSize - currentSize));
        
        for (int i = 0; i < toAdd; i++) {
            pool.offer(creator.get());
        }
    }
    
    public void drain() {
        List<T> drained = new ArrayList<>();
        pool.drainTo(drained);
    }
    
    public boolean isFull() {
        return pool.size() >= maxSize;
    }
}