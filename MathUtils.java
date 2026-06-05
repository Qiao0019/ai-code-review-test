package com.test.pitfalls;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MathUtils {
    
    public static final double EPSILON = 1e-10;
    
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }
    
    public static boolean equals(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }
    
    public static double round(double value, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places cannot be negative");
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static String formatNumber(double value, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }
    
    public static boolean isInteger(double value) {
        return equals(value, Math.floor(value));
    }
    
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static double percentage(double part, double total) {
        if (total == 0) {
            return 0;
        }
        return (part / total) * 100;
    }
    
    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    
    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return Math.abs(a * b) / gcd(a, b);
    }
    
    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        int i = 5;
        while (i * i <= n) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
            i += 6;
        }
        return true;
    }
    
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    
    public static double manhattanDistance(double x1, double y1, double x2, double y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }
    
    public static double average(double[] values) {
        if (values == null || values.length == 0) {
            return 0;
        }
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }
    
    public static double median(double[] values) {
        if (values == null || values.length == 0) {
            return 0;
        }
        double[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int n = sorted.length;
        if (n % 2 == 0) {
            return (sorted[n / 2 - 1] + sorted[n / 2]) / 2;
        } else {
            return sorted[n / 2];
        }
    }
    
    public static long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}