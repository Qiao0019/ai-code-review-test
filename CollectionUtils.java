package com.test.pitfalls;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionUtils {
    
    public static <T> List<T> filterNull(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public static <T> Optional<T> findFirst(List<T> list, Predicate<T> predicate) {
        if (list == null || predicate == null) {
            return Optional.empty();
        }
        return list.stream()
                .filter(predicate)
                .findFirst();
    }
    
    public static <T> List<T> reverse(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }
    
    public static <T> boolean containsAny(Collection<T> source, Collection<T> targets) {
        if (source == null || targets == null) {
            return false;
        }
        for (T item : targets) {
            if (source.contains(item)) {
                return true;
            }
        }
        return false;
    }
    
    public static <K, V> Map<K, V> toMap(List<V> list, Function<V, K> keyExtractor) {
        if (list == null || keyExtractor == null) {
            return Collections.emptyMap();
        }
        return list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyExtractor, v -> v, (v1, v2) -> v1));
    }
    
    public static String join(Collection<String> collection, String delimiter) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        return String.join(delimiter, collection);
    }
}