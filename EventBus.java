package com.test.pitfalls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    
    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = new ConcurrentHashMap<>();
    
    public <E extends Event> void subscribe(Class<E> eventType, EventListener<E> listener) {
        if (eventType == null || listener == null) {
            return;
        }
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
    
    public <E extends Event> void unsubscribe(Class<E> eventType, EventListener<E> listener) {
        List<EventListener<? extends Event>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }
    
    public <E extends Event> void publish(E event) {
        if (event == null) {
            return;
        }
        List<EventListener<? extends Event>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<? extends Event> listener : eventListeners) {
                try {
                    @SuppressWarnings("unchecked")
                    EventListener<E> typedListener = (EventListener<E>) listener;
                    typedListener.onEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public <E extends Event> int getListenerCount(Class<E> eventType) {
        List<EventListener<? extends Event>> eventListeners = listeners.get(eventType);
        return eventListeners != null ? eventListeners.size() : 0;
    }
    
    public void clearAllListeners() {
        listeners.clear();
    }
    
    public interface EventListener<E extends Event> {
        void onEvent(E event);
    }
    
    public static abstract class Event {
        private final long timestamp;
        
        protected Event() {
            this.timestamp = System.currentTimeMillis();
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}