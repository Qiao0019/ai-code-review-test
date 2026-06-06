package com.test.pitfalls;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsCounter {
    
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final CounterConfig config;
    
    public MetricsCounter() {
        this.config = CounterConfig.defaultConfig();
    }
    
    public MetricsCounter(CounterConfig config) {
        this.config = config != null ? config : CounterConfig.defaultConfig();
    }
    
    public static class CounterConfig {
        private final long maxValue;
        private final boolean autoResetOnMax;
        
        public CounterConfig(long maxValue, boolean autoResetOnMax) {
            this.maxValue = maxValue;
            this.autoResetOnMax = autoResetOnMax;
        }
        
        public static CounterConfig defaultConfig() {
            return new CounterConfig(Long.MAX_VALUE, false);
        }
        
        public long getMaxValue() {
            return maxValue;
        }
        
        public boolean isAutoResetOnMax() {
            return autoResetOnMax;
        }
    }
    
    public void increment(String key) {
        validateKey(key);
        AtomicLong counter = counters.computeIfAbsent(key, k -> new AtomicLong(0));
        long newValue = counter.incrementAndGet();
        if (config.isAutoResetOnMax() && newValue >= config.getMaxValue()) {
            counter.set(0);
        }
    }
    
    public void decrement(String key) {
        validateKey(key);
        counters.computeIfAbsent(key, k -> new AtomicLong(0)).decrementAndGet();
    }
    
    public long get(String key) {
        validateKey(key);
        AtomicLong counter = counters.get(key);
        return counter != null ? counter.get() : 0;
    }
    
    public void set(String key, long value) {
        validateKey(key);
        counters.computeIfAbsent(key, k -> new AtomicLong(0)).set(value);
    }
    
    public void reset(String key) {
        validateKey(key);
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
    
    public Map<String, Long> snapshot() {
        Map<String, Long> snapshot = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().get());
        }
        return Collections.unmodifiableMap(snapshot);
    }
    
    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
    }
}