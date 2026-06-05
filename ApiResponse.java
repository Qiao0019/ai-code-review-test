package com.test.pitfalls;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    
    private int code;
    private String message;
    private T data;
    private String timestamp;
    
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().toString();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
    
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Created", data);
    }
    
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(204, "No Content", null);
    }
    
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null);
    }
    
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null);
    }
    
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null);
    }
    
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
    
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(500, message, null);
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
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
    
    public static <T> ApiResponse<T> accepted(T data) {
        return new ApiResponse<>(202, "Accepted", data);
    }
    
    public static <T> ApiResponse<T> movedPermanently(String location) {
        ApiResponse<T> response = new ApiResponse<>(301, "Moved Permanently", null);
        response.message = "Location: " + location;
        return response;
    }
    
    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<>(409, message, null);
    }
    
    public static <T> ApiResponse<T> tooManyRequests(String message) {
        return new ApiResponse<>(429, message, null);
    }
    
    public static <T> ApiResponse<T> serviceUnavailable(String message) {
        return new ApiResponse<>(503, message, null);
    }
    
    public ApiResponse<T> withCode(int code) {
        this.code = code;
        return this;
    }
    
    public ApiResponse<T> withMessage(String message) {
        this.message = message;
        return this;
    }
    
    public ApiResponse<T> withData(T data) {
        this.data = data;
        return this;
    }
    
    public static <T> ApiResponse<T> pagination(List<T> data, long total, int page, int pageSize) {
        Map<String, Object> paginationData = new HashMap<>();
        paginationData.put("items", data);
        paginationData.put("total", total);
        paginationData.put("page", page);
        paginationData.put("pageSize", pageSize);
        paginationData.put("totalPages", (total + pageSize - 1) / pageSize);
        return new ApiResponse<>(200, "Success", (T) paginationData);
    }
}