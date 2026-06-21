package com.test.pitfalls;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiResponse<T> {
    
    private final int code;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    
    private ApiResponse(int code, String message, T data, Map<String, Object> metadata) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }
    
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Created", data, null);
    }
    
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(204, "No Content", null, null);
    }
    
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null, null);
    }
    
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null, null);
    }
    
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null, null);
    }
    
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null, null);
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, null);
    }
    
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(500, message, null, null);
    }
    
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }
    
    public static class ApiResponseBuilder<T> {
        private int code = 200;
        private String message = "Success";
        private T data;
        private Map<String, Object> metadata = new HashMap<>();
        
        public ApiResponseBuilder<T> code(int code) {
            this.code = code;
            return this;
        }
        
        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }
        
        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }
        
        public ApiResponseBuilder<T> addMeta(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public ApiResponse<T> build() {
            return new ApiResponse<>(code, message, data, metadata);
        }
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public T getData() {
        return data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
    
    public static <T> ApiResponse<T> pagination(List<T> data, long total, int page, int pageSize) {
        Map<String, Object> paginationData = new HashMap<>();
        paginationData.put("items", data);
        paginationData.put("total", total);
        paginationData.put("page", page);
        paginationData.put("pageSize", pageSize);
        paginationData.put("totalPages", (total + pageSize - 1) / pageSize);
        return builder().data((T) paginationData).addMeta("pagination", true).build();
    }
}