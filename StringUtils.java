package com.test.pitfalls;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
    
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
    
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }
    
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
    
    public static List<String> split(String str, String delimiter) {
        if (isBlank(str) || delimiter == null) {
            return List.of();
        }
        return Arrays.stream(str.split(delimiter))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    public static String camelCase(String str) {
        if (isBlank(str)) {
            return str;
        }
        List<String> words = split(str, "[_\\s-]+");
        if (words.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder(words.get(0).toLowerCase());
        for (int i = 1; i < words.size(); i++) {
            result.append(capitalize(words.get(i)));
        }
        return result.toString();
    }
    
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }
    
    public static String mask(String str, int start, int end, char maskChar) {
        if (isBlank(str) || start < 0 || end < 0 || start >= end) {
            return str;
        }
        int length = str.length();
        if (end > length) {
            end = length;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = start; i < end; i++) {
            sb.setCharAt(i, maskChar);
        }
        return sb.toString();
    }
    
    public static String maskEmail(String email) {
        if (isBlank(email)) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "*".repeat(Math.min(4, atIndex - 1)) + email.substring(atIndex);
    }
    
    public static String maskPhone(String phone) {
        if (isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    public static String repeat(String str, int count) {
        if (isBlank(str) || count <= 0) {
            return "";
        }
        return str.repeat(count);
    }
    
    public static String padStart(String str, int minLength, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= minLength) {
            return str;
        }
        return String.valueOf(padChar).repeat(minLength - str.length()) + str;
    }
    
    public static String padEnd(String str, int minLength, char padChar) {
        if (str == null) {
            str = "";
        }
        if (str.length() >= minLength) {
            return str;
        }
        return str + String.valueOf(padChar).repeat(minLength - str.length());
    }
}