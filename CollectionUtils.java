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
    
    public static <T> List<T> distinct(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }
    
    public static <T> List<T> take(List<T> list, int n) {
        if (list == null || n <= 0) {
            return Collections.emptyList();
        }
        return list.stream()
                .limit(n)
                .collect(Collectors.toList());
    }
    
    public static <T> List<T> drop(List<T> list, int n) {
        if (list == null || n <= 0) {
            return list != null ? new ArrayList<>(list) : Collections.emptyList();
        }
        return list.stream()
                .skip(n)
                .collect(Collectors.toList());
    }
    
    public static <T> List<T> sort(List<T> list, Comparator<T> comparator) {
        if (list == null) {
            return Collections.emptyList();
        }
        if (comparator == null) {
            return new ArrayList<>(list);
        }
        List<T> result = new ArrayList<>(list);
        result.sort(comparator);
        return result;
    }
    
    public static <T> Optional<T> last(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size() - 1));
    }
    
    public static <T> Map<T, Long> groupByCount(List<T> list) {
        if (list == null) {
            return Collections.emptyMap();
        }
        return list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
    }
}