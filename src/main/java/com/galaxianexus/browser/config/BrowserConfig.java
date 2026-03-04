package com.galaxianexus.browser.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.galaxianexus.browser.GalaxiaNexusBrowserMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BrowserConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "config/galaxianexus-browser.json";
    
    private BrowserMode mode = BrowserMode.EMBEDDED;
    private boolean enableJavaScript = true;
    private boolean enableCookies = false;
    private int browserWidth = 1024;
    private int browserHeight = 768;
    private String defaultUrl = "about:blank";
    
    public enum BrowserMode {
        EMBEDDED,  // Use embedded browser with JavaScript support
        EXTERNAL   // Open in system browser
    }
    
    public static BrowserConfig load() {
        File configFile = new File(CONFIG_FILE);
        
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                BrowserConfig config = new BrowserConfig();
                
                if (json.has("mode")) {
                    config.mode = BrowserMode.valueOf(json.get("mode").getAsString().toUpperCase());
                }
                if (json.has("enableJavaScript")) {
                    config.enableJavaScript = json.get("enableJavaScript").getAsBoolean();
                }
                if (json.has("enableCookies")) {
                    config.enableCookies = json.get("enableCookies").getAsBoolean();
                }
                if (json.has("browserWidth")) {
                    config.browserWidth = json.get("browserWidth").getAsInt();
                }
                if (json.has("browserHeight")) {
                    config.browserHeight = json.get("browserHeight").getAsInt();
                }
                if (json.has("defaultUrl")) {
                    config.defaultUrl = json.get("defaultUrl").getAsString();
                }
                
                GalaxiaNexusBrowserMod.LOGGER.info("Loaded browser config: mode={}, js={}", 
                    config.mode, config.enableJavaScript);
                return config;
            } catch (Exception e) {
                GalaxiaNexusBrowserMod.LOGGER.error("Failed to load config, using defaults", e);
            }
        }
        
        // Create default config
        BrowserConfig config = new BrowserConfig();
        config.save();
        return config;
    }
    
    public void save() {
        try {
            File configFile = new File(CONFIG_FILE);
            configFile.getParentFile().mkdirs();
            
            JsonObject json = new JsonObject();
            json.addProperty("mode", mode.name());
            json.addProperty("enableJavaScript", enableJavaScript);
            json.addProperty("enableCookies", enableCookies);
            json.addProperty("browserWidth", browserWidth);
            json.addProperty("browserHeight", browserHeight);
            json.addProperty("defaultUrl", defaultUrl);
            
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(json, writer);
            }
            
            GalaxiaNexusBrowserMod.LOGGER.info("Saved browser config");
        } catch (IOException e) {
            GalaxiaNexusBrowserMod.LOGGER.error("Failed to save config", e);
        }
    }
    
    public BrowserMode getMode() {
        return mode;
    }
    
    public void setMode(BrowserMode mode) {
        this.mode = mode;
    }
    
    public boolean isEnableJavaScript() {
        return enableJavaScript;
    }
    
    public void setEnableJavaScript(boolean enableJavaScript) {
        this.enableJavaScript = enableJavaScript;
    }
    
    public boolean isEnableCookies() {
        return enableCookies;
    }
    
    public void setEnableCookies(boolean enableCookies) {
        this.enableCookies = enableCookies;
    }
    
    public int getBrowserWidth() {
        return browserWidth;
    }
    
    public void setBrowserWidth(int browserWidth) {
        this.browserWidth = browserWidth;
    }
    
    public int getBrowserHeight() {
        return browserHeight;
    }
    
    public void setBrowserHeight(int browserHeight) {
        this.browserHeight = browserHeight;
    }
    
    public String getDefaultUrl() {
        return defaultUrl;
    }
    
    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }
}
