package com.galaxianexus.browser;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BrowserScreen extends Screen {
    private static final int TOOLBAR_HEIGHT = 50;
    private static final int AVATAR_SIZE = 32;
    
    private final Screen parent;
    private String currentUrl;
    private TextFieldWidget urlField;
    private BrowserManager browserManager;
    private WebContentRenderer contentRenderer;
    
    private Identifier playerAvatarTexture;
    private boolean avatarLoaded = false;

    protected BrowserScreen(Screen parent, String initialUrl) {
        super(Text.literal("GalaxiaNexus Browser"));
        this.parent = parent;
        this.currentUrl = initialUrl;
        this.browserManager = GalaxiaNexusBrowserMod.getInstance().getBrowserManager();
        this.contentRenderer = new WebContentRenderer();
    }
    
    public static void open(String url) {
        MinecraftClient client = MinecraftClient.getInstance();
        BrowserManager manager = GalaxiaNexusBrowserMod.getInstance().getBrowserManager();
        String injectedUrl = manager.injectPlayerData(url);
        client.execute(() -> client.setScreen(new BrowserScreen(client.currentScreen, injectedUrl)));
    }

    @Override
    protected void init() {
        super.init();
        
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
        
        // Open External button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("External"),
            button -> {
                browserManager.openExternalBrowser(urlField.getText());
                if (client != null) {
                    client.player.sendMessage(Text.literal("§aOpened in external browser"), false);
                }
            }
        ).dimensions(this.width - 95, toolbarY + 5, 60, 20).build());
        
        // Close button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Close"),
            button -> close()
        ).dimensions(this.width - 30, toolbarY + 5, 25, 20).build());
        
        // Load player avatar
        loadPlayerAvatar();
        
        // Load web content
        if (currentUrl != null && !currentUrl.equals("about:blank")) {
            contentRenderer.loadUrl(currentUrl);
        }
    }
    
    private void navigateToUrl(String url) {
        if (url != null && !url.isEmpty()) {
            this.currentUrl = browserManager.injectPlayerData(url);
            this.urlField.setText(this.currentUrl);
            contentRenderer.loadUrl(this.currentUrl);
        }
    }
    
    private void loadPlayerAvatar() {
        String avatarUrl = browserManager.getPlayerAvatarUrl(AVATAR_SIZE);
        if (avatarUrl == null) {
            return;
        }
        
        new Thread(() -> {
            try {
                URL url = new URL(avatarUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                try (InputStream inputStream = connection.getInputStream()) {
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    
                    if (bufferedImage != null) {
                        // Convert BufferedImage to NativeImage
                        NativeImage nativeImage = new NativeImage(bufferedImage.getWidth(), bufferedImage.getHeight(), true);
                        for (int y = 0; y < bufferedImage.getHeight(); y++) {
                            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                                int rgb = bufferedImage.getRGB(x, y);
                                nativeImage.setColorArgb(x, y, rgb);
                            }
                        }
                        
                        // Register texture on main thread
                        if (client != null) {
                            client.execute(() -> {
                                playerAvatarTexture = Identifier.of("galaxianexus", "player_avatar");
                                client.getTextureManager().registerTexture(
                                    playerAvatarTexture,
                                    new NativeImageBackedTexture(nativeImage)
                                );
                                avatarLoaded = true;
                            });
                        }
                    }
                }
            } catch (Exception e) {
                GalaxiaNexusBrowserMod.LOGGER.error("Failed to load player avatar", e);
            }
        }, "Avatar-Loader").start();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Render toolbar background
        context.fill(0, 0, this.width, TOOLBAR_HEIGHT, 0xCC000000);
        
        // Render player avatar
        if (avatarLoaded && playerAvatarTexture != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            context.drawTexture(RenderLayer::getGuiTextured, playerAvatarTexture, 10, 10, 0.0f, 0.0f, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE, AVATAR_SIZE);
        }
        
        // Render URL field
        urlField.render(context, mouseX, mouseY, delta);
        
        // Render buttons
        super.render(context, mouseX, mouseY, delta);
        
        // Render web content area
        int contentY = TOOLBAR_HEIGHT + 10;
        int contentHeight = this.height - contentY - 10;
        
        // Content background
        context.fill(10, contentY, this.width - 10, this.height - 10, 0xFF1E1E1E);
        
        // Render web content
        contentRenderer.render(context, 10, contentY, this.width - 20, contentHeight);
        
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
        
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
