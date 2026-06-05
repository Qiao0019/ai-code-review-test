package com.test.pitfalls;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class DataProcessor {
    
    private static final String DB_PASSWORD_ENV = "DB_PASSWORD";
    private static final String API_KEY_ENV = "API_KEY";
    
    public void processCommand(String[] commandArgs) {
        if (commandArgs == null || commandArgs.length == 0) {
            throw new IllegalArgumentException("Command arguments cannot be empty");
        }
        
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(commandArgs);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to execute command", e);
        }
    }
    
    public void saveUserData(String userData, String filename) throws IOException {
        if (userData == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or blank");
        }
        
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(userData);
        }
    }
    
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        // In real code, you would check against a secure user store
        return "admin".equals(username) && "admin123".equals(password);
    }
    
    public String formatDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    
    public void queryDatabase(String userId) throws SQLException {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        
        String dbPassword = System.getenv(DB_PASSWORD_ENV);
        String url = "jdbc:mysql://localhost/db";
        String user = "root";
        
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getString("name"));
                }
            }
        }
    }
    
    public int[] copyArray(int[] source) {
        if (source == null) {
            throw new IllegalArgumentException("Source array cannot be null");
        }
        return Arrays.copyOf(source, source.length);
    }
    
    public double calculateAverage(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Numbers list cannot be null or empty");
        }
        
        long sum = 0;
        for (Integer num : numbers) {
            if (num == null) {
                throw new IllegalArgumentException("List contains null element");
            }
            sum += num;
        }
        return (double) sum / numbers.size();
    }
    
    public void logError(String message, Exception e) {
        if (message != null) {
            System.err.println("Error: " + message);
        }
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    public String generateToken(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            UUID.fromString(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public String readFileAsString(String path) throws IOException {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path cannot be null or blank");
        }
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    public void writeStringToFile(String content, String path) throws IOException {
        if (content == null || path == null || path.isBlank()) {
            throw new IllegalArgumentException("Content and path cannot be null or blank");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(content);
        }
    }
    
    public Map<String, String> parseQueryString(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return Collections.emptyMap();
        }
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}