package com.test.pitfalls;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsCounter {
    
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    
    public void increment(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
        counters.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    public void incrementBy(String key, long delta) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
        counters.computeIfAbsent(key, k -> new AtomicLong(0)).addAndGet(delta);
    }
    
    public void decrement(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
        counters.computeIfAbsent(key, k -> new AtomicLong(0)).decrementAndGet();
    }
    
    public long get(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
        AtomicLong counter = counters.get(key);
        return counter != null ? counter.get() : 0;
    }
    
    public void reset(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
        counters.remove(key);
    }
    
    public void resetAll() {
        counters.clear();
    }
    
    public long getTotal() {
        return counters.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
    }
    
    public int getKeyCount() {
        return counters.size();
    }
    
    public boolean hasKey(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        return counters.containsKey(key);
    }
}