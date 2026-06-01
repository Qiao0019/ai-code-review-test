package com.test.pitfalls;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserInputValidator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z][a-zA-Z0-9_]{3,19}$"
    );
    
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 32;
    
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return ValidationResult.failure("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationResult.failure("Invalid email format");
        }
        return ValidationResult.success();
    }
    
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return ValidationResult.failure("Phone number cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return ValidationResult.failure("Invalid phone number format");
        }
        return ValidationResult.success();
    }
    
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.isBlank()) {
            return ValidationResult.failure("Username cannot be empty");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return ValidationResult.failure(
                    "Username must be 4-20 characters, start with letter, and contain only letters, numbers, underscore"
            );
        }
        return ValidationResult.success();
    }
    
    public static ValidationResult validatePassword(String password) {
        if (password == null) {
            return ValidationResult.failure("Password cannot be null");
        }
        
        List<String> errors = new ArrayList<>();
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.add("Password must not exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            errors.add("Password must contain at least one uppercase letter");
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            errors.add("Password must contain at least one lowercase letter");
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            errors.add("Password must contain at least one digit");
        }
        
        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, List.of());
        }
        
        public static ValidationResult failure(String error) {
            return new ValidationResult(false, List.of(error));
        }
        
        public static ValidationResult failure(List<String> errors) {
            return new ValidationResult(false, errors);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}