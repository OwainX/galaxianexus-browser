package com.galaxianexus.browser;

import com.galaxianexus.browser.config.BrowserConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.montoyo.mcef.api.*;

/**
 * Browser screen using MCEF (Minecraft Chromium Embedded Framework)
 * Provides full JavaScript, AJAX, and modern web support
 */
public class MCEFBrowserScreen extends Screen {
    private static final int TOOLBAR_HEIGHT = 50;
    private static final int AVATAR_SIZE = 32;
    
    private final Screen parent;
    private String currentUrl;
    private TextFieldWidget urlField;
    private BrowserManager browserManager;
    private BrowserConfig config;
    
    private IBrowser browser;
    private Identifier playerAvatarTexture;
    private boolean avatarLoaded = false;
    private boolean mcefAvailable = false;

    protected MCEFBrowserScreen(Screen parent, String initialUrl, BrowserConfig config) {
        super(Text.literal("GalaxiaNexus Browser"));
        this.parent = parent;
        this.currentUrl = initialUrl;
        this.browserManager = GalaxiaNexusBrowserMod.getInstance().getBrowserManager();
        this.config = config;
        
        // Check if MCEF is available
        try {
            API api = APIHolder.getAPI();
            this.mcefAvailable = (api != null);
            if (!mcefAvailable) {
                GalaxiaNexusBrowserMod.LOGGER.warn("MCEF API not available, falling back to external browser");
            }
        } catch (Exception e) {
            GalaxiaNexusBrowserMod.LOGGER.error("MCEF not available", e);
            this.mcefAvailable = false;
        }
    }
    
    public static void open(String url, BrowserConfig config) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            MCEFBrowserScreen screen = new MCEFBrowserScreen(client.currentScreen, url, config);
            client.setScreen(screen);
        });
    }

    @Override
    protected void init() {
        super.init();
        
        // If MCEF is not available, show message and open external browser
        if (!mcefAvailable) {
            if (client != null) {
                client.setScreen(parent);
                browserManager.openExternalBrowser(currentUrl);
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("§cMCEF not available. Opening in external browser..."), false);
                }
            }
            return;
        }
        
        int toolbarY = 10;
        
        // URL text field
        this.urlField = new TextFieldWidget(
            this.textRenderer,
            AVATAR_SIZE + 20,
            toolbarY + 5,
            this.width - AVATAR_SIZE - 170,
            20,
            Text.literal("URL")
        );
        this.urlField.setMaxLength(2048);
        this.urlField.setText(currentUrl);
        this.addSelectableChild(this.urlField);
        
        // Go button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Go"),
            button -> navigateToUrl(urlField.getText())
        ).dimensions(this.width - 140, toolbarY + 5, 40, 20).build());
        
        // Reload button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("↻"),
            button -> {
                if (browser != null) {
                    browser.reload();
                }
            }
        ).dimensions(this.width - 95, toolbarY + 5, 25, 20).build());
        
        // Close button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Close"),
            button -> close()
        ).dimensions(this.width - 65, toolbarY + 5, 60, 20).build());
        
        // Initialize MCEF browser
        try {
            API api = APIHolder.getAPI();
            if (api != null) {
                int browserWidth = this.width - 20;
                int browserHeight = this.height - TOOLBAR_HEIGHT - 20;
                
                browser = api.createBrowser(currentUrl, config.isEnableJavaScript());
                browser.resize(browserWidth, browserHeight);
                
                GalaxiaNexusBrowserMod.LOGGER.info("MCEF browser initialized: {}x{}", browserWidth, browserHeight);
            }
        } catch (Exception e) {
            GalaxiaNexusBrowserMod.LOGGER.error("Failed to create MCEF browser", e);
        }
        
        // Load player avatar
        loadPlayerAvatar();
    }
    
    private void navigateToUrl(String url) {
        if (url != null && !url.isEmpty()) {
            this.currentUrl = browserManager.injectPlayerData(url);
            this.urlField.setText(this.currentUrl);
            
            if (browser != null) {
                browser.loadURL(this.currentUrl);
            }
        }
    }
    
    private void loadPlayerAvatar() {
        String avatarUrl = browserManager.getPlayerAvatarUrl(AVATAR_SIZE);
        if (avatarUrl == null) {
            return;
        }
        
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(avatarUrl);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                try (java.io.InputStream inputStream = connection.getInputStream()) {
                    java.awt.image.BufferedImage bufferedImage = javax.imageio.ImageIO.read(inputStream);
                    
                    if (bufferedImage != null && client != null) {
                        NativeImage nativeImage = new NativeImage(bufferedImage.getWidth(), bufferedImage.getHeight(), true);
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {
                            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                                nativeImage.setColor(x, y, bufferedImage.getRGB(x, y));
                            }
                        }
                        
                        client.execute(() -> {
                            playerAvatarTexture = client.getTextureManager().registerDynamicTexture(
                                "player_avatar", 
                                new NativeImageBackedTexture(nativeImage)
                            );
                            avatarLoaded = true;
                        });
                    }
                }
            } catch (Exception e) {
                GalaxiaNexusBrowserMod.LOGGER.error("Failed to load player avatar", e);
            }
        }, "Avatar-Loader").start();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!mcefAvailable) {
            return;
        }
        
        // Render background
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Render toolbar background
        context.fill(0, 0, this.width, TOOLBAR_HEIGHT, 0xCC000000);
        
        // Render player avatar
        if (avatarLoaded && playerAvatarTexture != null) {
            context.drawTexture(playerAvatarTexture, 10, 10, 0, 0, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE);
        }
        
        // Render URL field
        urlField.render(context, mouseX, mouseY, delta);
        
        // Render buttons
        super.render(context, mouseX, mouseY, delta);
        
        // Render MCEF browser content
        if (browser != null) {
            int contentY = TOOLBAR_HEIGHT + 10;
            int contentHeight = this.height - contentY - 10;
            
            // Draw browser texture
            browser.draw(context, 10, contentY, this.width - 20, contentHeight);
        }
        
        // Player info overlay
        if (browserManager.getPlayerName() != null) {
            String playerInfo = "§7Player: §f" + browserManager.getPlayerName();
            context.drawTextWithShadow(
                this.textRenderer, 
                playerInfo, 
                AVATAR_SIZE + 20, 
                35, 
                0xFFFFFF
            );
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // Enter or Numpad Enter
            navigateToUrl(urlField.getText());
            return true;
        }
        
        if (urlField.isFocused()) {
            return urlField.keyPressed(keyCode, scanCode, modifiers);
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (urlField.mouseClicked(mouseX, mouseY, button)) {
            setFocused(urlField);
            return true;
        }
        
        // Pass mouse events to MCEF browser
        if (browser != null && mouseY > TOOLBAR_HEIGHT) {
            browser.injectMouseMove(
                (int) mouseX - 10, 
                (int) mouseY - TOOLBAR_HEIGHT - 10
            );
            browser.injectMouseButton(
                (int) mouseX - 10, 
                (int) mouseY - TOOLBAR_HEIGHT - 10, 
                button, 
                true, 
                1
            );
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (browser != null && mouseY > TOOLBAR_HEIGHT) {
            browser.injectMouseWheel(
                (int) mouseX - 10,
                (int) mouseY - TOOLBAR_HEIGHT - 10,
                (int) verticalAmount,
                (int) horizontalAmount
            );
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        if (browser != null) {
            browser.close();
        }
        
        if (client != null) {
            client.setScreen(parent);
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
