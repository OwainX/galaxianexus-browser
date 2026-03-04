package com.galaxianexus.browser;

import com.galaxianexus.browser.config.BrowserConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.awt.Desktop;
import java.net.URI;
import java.util.UUID;

public class BrowserManager {
    private final MinecraftClient client;
    private final BrowserConfig config;
    
    public BrowserManager(BrowserConfig config) {
        this.client = MinecraftClient.getInstance();
        this.config = config;
    }
    
    /**
     * Opens a browser based on the configured mode
     */
    public void openBrowser(String url) {
        UUID playerUUID = getPlayerUUID();
        if (playerUUID == null) {
            GalaxiaNexusBrowserMod.LOGGER.error("Cannot open browser: player UUID is null");
            return;
        }
        
        switch (config.getMode()) {
            case EMBEDDED:
                // Open MCEF embedded browser with full JavaScript/AJAX support
                client.execute(() -> {
                    MCEFBrowserScreen screen = new MCEFBrowserScreen(url, playerUUID.toString());
                    client.setScreen(screen);
                });
                GalaxiaNexusBrowserMod.LOGGER.info("Opening embedded browser for: {}", url);
                break;
            case EXTERNAL:
                // Open in system browser as fallback (for low-end systems)
                openExternalBrowser(injectPlayerData(url));
                if (client != null && client.player != null) {
                    client.player.sendMessage(Text.literal("§aOpened in external browser"), false);
                }
                break;
        }
    }
    
    /**
     * Gets the current player's UUID
     */
    public UUID getPlayerUUID() {
        if (client.player != null) {
            return client.player.getUuid();
        }
        return null;
    }
    
    /**
     * Gets the current player's username
     */
    public String getPlayerName() {
        if (client.player != null) {
            return client.player.getName().getString();
        }
        return null;
    }
    
    /**
     * Injects player UUID into a URL as a query parameter
     */
    public String injectUUID(String url) {
        UUID playerUUID = getPlayerUUID();
        if (playerUUID == null) {
            return url;
        }
        
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + "player_uuid=" + playerUUID.toString();
    }
    
    /**
     * Injects player UUID and name into a URL
     */
    public String injectPlayerData(String url) {
        UUID playerUUID = getPlayerUUID();
        String playerName = getPlayerName();
        
        if (playerUUID == null) {
            return url;
        }
        
        String separator = url.contains("?") ? "&" : "?";
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append(separator).append("player_uuid=").append(playerUUID.toString());
        
        if (playerName != null) {
            urlBuilder.append("&player_name=").append(playerName);
        }
        
        return urlBuilder.toString();
    }
    
    /**
     * Opens a URL in the system's default external browser with UUID injected
     */
    public void openExternalBrowser(String url) {
        try {
            String injectedUrl = injectPlayerData(url);
            
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(injectedUrl));
                GalaxiaNexusBrowserMod.LOGGER.info("Opened URL in external browser: {}", injectedUrl);
            } else {
                GalaxiaNexusBrowserMod.LOGGER.error("Desktop browsing not supported on this system");
            }
        } catch (Exception e) {
            GalaxiaNexusBrowserMod.LOGGER.error("Failed to open external browser", e);
        }
    }
    
    /**
     * Gets the URL for the player's skin avatar
     * Uses Crafatar API for rendering Minecraft skins
     */
    public String getPlayerAvatarUrl(int size) {
        UUID playerUUID = getPlayerUUID();
        if (playerUUID == null) {
            return null;
        }
        
        // Crafatar provides Minecraft avatar rendering
        return String.format("https://crafatar.com/avatars/%s?size=%d&overlay", 
                           playerUUID.toString(), size);
    }
    
    /**
     * Gets the URL for the player's full body skin render
     */
    public String getPlayerSkinUrl() {
        UUID playerUUID = getPlayerUUID();
        if (playerUUID == null) {
            return null;
        }
        
        return String.format("https://crafatar.com/renders/body/%s?overlay", 
                           playerUUID.toString());
    }
}
