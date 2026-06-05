package com.test.pitfalls;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }
    
    public static boolean isToday(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.equals(LocalDate.now());
    }
    
    public static LocalDate addDays(LocalDate date, int days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }
    
    public static LocalDateTime addHours(LocalDateTime dateTime, int hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }
    
    public static boolean isBefore(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isBefore(date2);
    }
    
    public static boolean isAfter(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isAfter(date2);
    }
    
    public static LocalDate startOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(1);
    }
    
    public static LocalDate endOfMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.withDayOfMonth(date.lengthOfMonth());
    }
    
    public static LocalDate startOfWeek(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }
    
    public static LocalDate endOfWeek(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.plusDays(7 - date.getDayOfWeek().getValue());
    }
    
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return (date.isEqual(start) || date.isAfter(start)) && 
               (date.isEqual(end) || date.isBefore(end));
    }
    
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(dateTime, now);
        
        if (seconds < 60) {
            return "Just now";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + " minutes ago";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " hours ago";
        }
        long days = hours / 24;
        if (days < 30) {
            return days + " days ago";
        }
        return formatDate(dateTime.toLocalDate());
    }
    
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }
}