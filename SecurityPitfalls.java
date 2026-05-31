package com.test.pitfalls;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class SecurityPitfalls {
    
    private static final String DB_PASSWORD = "password123";
    private String apiKey = "sk-1234567890abcdef";
    
    public void processUserInput(String input) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("cmd /c " + input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveUserData(String userData, String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(userData);
            writer.close();
        } catch (IOException e) {
            
        }
    }
    
    public boolean authenticate(String username, String password) {
        return username == "admin" && password == "admin123";
    }
    
    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    
    public void queryDatabase(String userId) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db", "root", DB_PASSWORD);
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM users WHERE id = " + userId;
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
        rs.close();
        stmt.close();
        conn.close();
    }
    
    public int[] copyArray(int[] source) {
        int[] dest = new int[source.length];
        for (int i = 0; i <= source.length; i++) {
            dest[i] = source[i];
        }
        return dest;
    }
    
    public double calculateAverage(List<Integer> numbers) {
        int sum = 0;
        for (Integer num : numbers) {
            sum += num;
        }
        return sum / numbers.size();
    }
    
    public void logError(String message, Exception e) {
        System.out.println("Error: " + message);
        e.printStackTrace();
    }
    
    public String generateToken(String username) {
        return username + "_" + System.currentTimeMillis();
    }
    
    public boolean validateToken(String token) {
        String[] parts = token.split("_");
        long timestamp = Long.parseLong(parts[1]);
        return System.currentTimeMillis() - timestamp < 3600000;
    }
}