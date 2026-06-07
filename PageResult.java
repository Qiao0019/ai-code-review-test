package com.test.pitfalls;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageResult<T> {
    
    private final List<T> data;
    private final int page;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final String sortBy;
    private final String sortDirection;
    private final Map<String, Object> metadata;
    
    private PageResult(Builder<T> builder) {
        this.data = builder.data != null ? List.copyOf(builder.data) : List.of();
        this.page = builder.page;
        this.pageSize = builder.pageSize;
        this.totalElements = Math.max(0, builder.totalElements);
        this.totalPages = calculateTotalPages();
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
        this.metadata = builder.metadata != null ? Map.copyOf(builder.metadata) : Map.of();
    }
    
    private int calculateTotalPages() {
        if (totalElements == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }
    
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    public static <T> PageResult<T> of(List<T> data, int page, int pageSize, long totalElements) {
        return builder()
                .data(data)
                .page(page)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .build();
    }
    
    public static <T> PageResult<T> empty(int page, int pageSize) {
        return builder()
                .data(List.of())
                .page(page)
                .pageSize(pageSize)
                .totalElements(0)
                .build();
    }
    
    public static <T> PageResult<T> from(List<T> allData, int page, int pageSize) {
        if (allData == null || allData.isEmpty()) {
            return empty(page, pageSize);
        }
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allData.size());
        
        List<T> pageData = start < allData.size() 
                ? allData.subList(start, end) 
                : List.of();
        
        return builder()
                .data(pageData)
                .page(page)
                .pageSize(pageSize)
                .totalElements(allData.size())
                .build();
    }
    
    public boolean hasNext() {
        return page < totalPages;
    }
    
    public boolean hasPrevious() {
        return page > 1;
    }
    
    public boolean isFirst() {
        return page == 1;
    }
    
    public boolean isLast() {
        return page >= totalPages;
    }
    
    public List<Integer> getNavigationPages(int maxVisible) {
        if (totalPages <= maxVisible) {
            return IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
        }
        
        int half = maxVisible / 2;
        int start = Math.max(1, page - half);
        int end = Math.min(totalPages, start + maxVisible - 1);
        
        if (end - start + 1 < maxVisible) {
            start = Math.max(1, end - maxVisible + 1);
        }
        
        return IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }
    
    public PageResult<T> withMetadata(String key, Object value) {
        Map<String, Object> newMetadata = new HashMap<>(this.metadata);
        newMetadata.put(key, value);
        return builder()
                .data(this.data)
                .page(this.page)
                .pageSize(this.pageSize)
                .totalElements(this.totalElements)
                .sortBy(this.sortBy)
                .sortDirection(this.sortDirection)
                .metadata(newMetadata)
                .build();
    }
    
    public <R> PageResult<R> map(Function<T, R> mapper) {
        List<R> mappedData = data.stream().map(mapper).collect(Collectors.toList());
        return builder()
                .data(mappedData)
                .page(page)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .metadata(metadata)
                .build();
    }
    
    public int getCurrentPage() {
        return page;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public List<T> getData() {
        return data;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public long getStartIndex() {
        if (totalElements == 0) {
            return 0;
        }
        return (long) (page - 1) * pageSize + 1;
    }
    
    public long getEndIndex() {
        if (totalElements == 0) {
            return 0;
        }
        return Math.min((long) page * pageSize, totalElements);
    }
    
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public int getCurrentSize() {
        return data.size();
    }
    
    public static class Builder<T> {
        private List<T> data;
        private int page = 1;
        private int pageSize = 10;
        private long totalElements = 0;
        private String sortBy;
        private String sortDirection = "asc";
        private Map<String, Object> metadata;
        
        public Builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }
        
        public Builder<T> page(int page) {
            if (page < 1) {
                throw new IllegalArgumentException("Page number must be at least 1");
            }
            this.page = page;
            return this;
        }
        
        public Builder<T> pageSize(int pageSize) {
            if (pageSize < 1) {
                throw new IllegalArgumentException("Page size must be at least 1");
            }
            this.pageSize = pageSize;
            return this;
        }
        
        public Builder<T> totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }
        
        public Builder<T> sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }
        
        public Builder<T> sortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }
        
        public Builder<T> metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public Builder<T> addMetadata(String key, Object value) {
            if (this.metadata == null) {
                this.metadata = new HashMap<>();
            }
            this.metadata.put(key, value);
            return this;
        }
        
        public PageResult<T> build() {
            return new PageResult<>(this);
        }
    }
}