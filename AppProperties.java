package com.test.pitfalls;

import java.time.Duration;

public class AppProperties {
    
    private Security security = new Security();
    private Database database = new Database();
    private Cache cache = new Cache();
    private Api api = new Api();
    
    public Security getSecurity() {
        return security;
    }
    
    public void setSecurity(Security security) {
        this.security = security;
    }
    
    public Database getDatabase() {
        return database;
    }
    
    public void setDatabase(Database database) {
        this.database = database;
    }
    
    public Cache getCache() {
        return cache;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public Api getApi() {
        return api;
    }
    
    public void setApi(Api api) {
        this.api = api;
    }
    
    public static class Security {
        private String jwtSecret = "default-secret-key";
        private Duration jwtExpiration = Duration.ofHours(24);
        private boolean enableCsrf = false;
        private int maxLoginAttempts = 5;
        
        public String getJwtSecret() {
            return jwtSecret;
        }
        
        public void setJwtSecret(String jwtSecret) {
            this.jwtSecret = jwtSecret;
        }
        
        public Duration getJwtExpiration() {
            return jwtExpiration;
        }
        
        public void setJwtExpiration(Duration jwtExpiration) {
            this.jwtExpiration = jwtExpiration;
        }
        
        public boolean isEnableCsrf() {
            return enableCsrf;
        }
        
        public void setEnableCsrf(boolean enableCsrf) {
            this.enableCsrf = enableCsrf;
        }
        
        public int getMaxLoginAttempts() {
            return maxLoginAttempts;
        }
        
        public void setMaxLoginAttempts(int maxLoginAttempts) {
            this.maxLoginAttempts = maxLoginAttempts;
        }
    }
    
    public static class Database {
        private String url = "jdbc:mysql://localhost:3306/example_db";
        private String username = "admin";
        private String password = "";
        private int connectionTimeout = 30000;
        private int maxPoolSize = 20;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public int getConnectionTimeout() {
            return connectionTimeout;
        }
        
        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }
        
        public int getMaxPoolSize() {
            return maxPoolSize;
        }
        
        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }
    
    public static class Cache {
        private boolean enabled = true;
        private Duration ttl = Duration.ofMinutes(5);
        private int maxEntries = 1000;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public Duration getTtl() {
            return ttl;
        }
        
        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }
        
        public int getMaxEntries() {
            return maxEntries;
        }
        
        public void setMaxEntries(int maxEntries) {
            this.maxEntries = maxEntries;
        }
    }
    
    public static class Api {
        private String basePath = "/api";
        private int maxRequestSize = 10485760;
        private int requestTimeout = 30000;
        
        public String getBasePath() {
            return basePath;
        }
        
        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
        
        public int getMaxRequestSize() {
            return maxRequestSize;
        }
        
        public void setMaxRequestSize(int maxRequestSize) {
            this.maxRequestSize = maxRequestSize;
        }
        
        public int getRequestTimeout() {
            return requestTimeout;
        }
        
        public void setRequestTimeout(int requestTimeout) {
            this.requestTimeout = requestTimeout;
        }
    }
}