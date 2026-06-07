package com.test.pitfalls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventBus {
    
    private final Map<Class<? extends Event>, List<PrioritizedListener<? extends Event>>> listeners = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private volatile boolean running = true;
    
    public EventBus() {
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "EventBus-Worker");
            t.setDaemon(true);
            return t;
        });
    }
    
    public EventBus(ExecutorService executor) {
        this.executor = executor != null ? executor : Executors.newCachedThreadPool();
    }
    
    public <E extends Event> void subscribe(Class<E> eventType, EventListener<E> listener) {
        subscribe(eventType, listener, 0);
    }
    
    public <E extends Event> void subscribe(Class<E> eventType, EventListener<E> listener, int priority) {
        if (eventType == null || listener == null) {
            throw new IllegalArgumentException("Event type and listener cannot be null");
        }
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                 .add(new PrioritizedListener<>(listener, priority));
    }
    
    public <E extends Event> void unsubscribe(Class<E> eventType, EventListener<E> listener) {
        List<PrioritizedListener<? extends Event>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.removeIf(pl -> pl.listener == listener);
        }
    }
    
    public <E extends Event> void publish(E event) {
        publish(event, null);
    }
    
    public <E extends Event> void publish(E event, Predicate<E> filter) {
        if (event == null || !running) {
            return;
        }
        
        List<PrioritizedListener<? extends Event>> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null || eventListeners.isEmpty()) {
            return;
        }
        
        List<PrioritizedListener<? extends Event>> sortedListeners = new ArrayList<>(eventListeners);
        sortedListeners.sort((a, b) -> Integer.compare(b.priority, a.priority));
        
        for (PrioritizedListener<? extends Event> pl : sortedListeners) {
            try {
                @SuppressWarnings("unchecked")
                EventListener<E> typedListener = (EventListener<E>) pl.listener;
                
                if (filter != null && !filter.test(event)) {
                    continue;
                }
                
                typedListener.onEvent(event);
            } catch (Exception e) {
                handleListenerException(e, event);
            }
        }
    }
    
    public <E extends Event> CompletableFuture<Void> publishAsync(E event) {
        return CompletableFuture.runAsync(() -> publish(event), executor);
    }
    
    public <E extends Event> void publishAsync(E event, Consumer<Throwable> errorHandler) {
        executor.submit(() -> {
            try {
                publish(event);
            } catch (Throwable t) {
                if (errorHandler != null) {
                    errorHandler.accept(t);
                }
            }
        });
    }
    
    public <E extends Event> int getListenerCount(Class<E> eventType) {
        List<PrioritizedListener<? extends Event>> eventListeners = listeners.get(eventType);
        return eventListeners != null ? eventListeners.size() : 0;
    }
    
    public int getTotalListenerCount() {
        return listeners.values().stream().mapToInt(List::size).sum();
    }
    
    public Set<Class<? extends Event>> getRegisteredEventTypes() {
        return listeners.keySet();
    }
    
    public void clearAllListeners() {
        listeners.clear();
    }
    
    public void shutdown() {
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    protected void handleListenerException(Exception e, Event event) {
        e.printStackTrace();
    }
    
    private static class PrioritizedListener<E extends Event> {
        final EventListener<E> listener;
        final int priority;
        
        PrioritizedListener(EventListener<E> listener, int priority) {
            this.listener = listener;
            this.priority = priority;
        }
    }
    
    public interface EventListener<E extends Event> {
        void onEvent(E event);
    }
    
    public static abstract class Event {
        private final long timestamp;
        private final String eventId;
        
        protected Event() {
            this.timestamp = System.currentTimeMillis();
            this.eventId = UUID.randomUUID().toString();
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getEventId() {
            return eventId;
        }
        
        public Class<? extends Event> getEventType() {
            return this.getClass();
        }
    }
}