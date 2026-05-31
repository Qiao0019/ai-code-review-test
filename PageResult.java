package com.test.pitfalls;

import java.util.List;

public class PageResult<T> {
    
    private final List<T> data;
    private final int page;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    
    public PageResult(List<T> data, int page, int pageSize, long totalElements) {
        if (page < 1) {
            throw new IllegalArgumentException("Page number must be at least 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }
        
        this.data = data != null ? List.copyOf(data) : List.of();
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = Math.max(0, totalElements);
        this.totalPages = calculateTotalPages();
    }
    
    private int calculateTotalPages() {
        if (totalElements == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / pageSize);
    }
    
    public static <T> PageResult<T> of(List<T> data, int page, int pageSize, long totalElements) {
        return new PageResult<>(data, page, pageSize, totalElements);
    }
    
    public static <T> PageResult<T> empty(int page, int pageSize) {
        return new PageResult<>(List.of(), page, pageSize, 0);
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
}