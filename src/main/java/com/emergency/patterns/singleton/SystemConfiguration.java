package com.emergency.patterns.singleton;

import java.util.Properties;
import java.time.LocalDateTime;

/**
 * Singleton Pattern - Manages global system configuration
 */
public class SystemConfiguration {
    
    private static SystemConfiguration instance;
    private final Properties config;
    private final LocalDateTime creationTime;
    
    private SystemConfiguration() {
        config = new Properties();
        creationTime = LocalDateTime.now();
        initializeDefaultConfig();
    }
    
    public static synchronized SystemConfiguration getInstance() {
        if (instance == null) {
            instance = new SystemConfiguration();
        }
        return instance;
    }
    
    private void initializeDefaultConfig() {
        config.setProperty("system.name", "Emergency Response System");
        config.setProperty("system.version", "2.0");
        config.setProperty("notification.interval", "5000");
        config.setProperty("max.concurrent.emergencies", "1000");
        config.setProperty("data.retention.days", "90");
        config.setProperty("auto.assignment.enabled", "true");
        config.setProperty("log.level", "INFO");
    }
    
    public String getProperty(String key) {
        return config.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        config.setProperty(key, value);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }
    
    public LocalDateTime getCreationTime() {
        return creationTime;
    }
    
    // Prevent cloning
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of SystemConfiguration is not allowed");
    }
}
