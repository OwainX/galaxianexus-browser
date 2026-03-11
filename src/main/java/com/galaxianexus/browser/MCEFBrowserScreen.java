package com.galaxianexus.browser;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.Text;

public class MCEFBrowserScreen extends Screen {
    private static final int BROWSER_DRAW_OFFSET = 0;
    
    private final String url;
    private final String playerUUID;
    private MCEFBrowser browser;

    public MCEFBrowserScreen(String url, String playerUUID) {
        super(Text.literal("GalaxiaNexus Browser"));
        this.url = url;
        this.playerUUID = playerUUID;
    }

    @Override
    protected void init() {
        super.init();
        if (browser == null) {
            String urlWithUUID = injectUUID(url);
            boolean transparent = false;
            browser = MCEF.createBrowser(urlWithUUID, transparent);
            resizeBrowser();
        }
    }

    private String injectUUID(String url) {
        if (url.contains("?")) {
            return url + "&player_uuid=" + playerUUID;
        } else {
            return url + "?player_uuid=" + playerUUID;
        }
    }

    private double getScaleFactor() {
        // Calculate scale factor manually (compatible with 1.21.11+)
        // In 1.21.11, getScaleFactor() was removed, so we calculate it from window dimensions
        return (double) client.getWindow().getWidth() / (double) client.getWindow().getScaledWidth();
    }

    private int mouseX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET) * getScaleFactor());
    }

    private int mouseY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET) * getScaleFactor());
    }

    private int scaleX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET * 2) * getScaleFactor());
    }

    private int scaleY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET * 2) * getScaleFactor());
    }

    private void resizeBrowser() {
        if (width > 100 && height > 100) {
            browser.resize(scaleX(width), scaleY(height));
        }
    }

    @Override
    public void removed() {
        if (browser != null) {
            browser.close();
        }
        super.removed();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        if (browser != null && browser.getRenderer() != null) {
            // Note: disableDepthTest/enableDepthTest removed in MC 1.21.11+
            // GUI rendering handles depth testing automatically
            RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            
            buffer.vertex(BROWSER_DRAW_OFFSET, height - BROWSER_DRAW_OFFSET, 0)
                  .texture(0.0f, 1.0f)
                  .color(255, 255, 255, 255);
            buffer.vertex(width - BROWSER_DRAW_OFFSET, height - BROWSER_DRAW_OFFSET, 0)
                  .texture(1.0f, 1.0f)
                  .color(255, 255, 255, 255);
            buffer.vertex(width - BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET, 0)
                  .texture(1.0f, 0.0f)
                  .color(255, 255, 255, 255);
            buffer.vertex(BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET, 0)
                  .texture(0.0f, 0.0f)
                  .color(255, 255, 255, 255);
            
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            RenderSystem.setShaderTexture(0, 0);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (browser != null) {
            browser.sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
            browser.setFocus(true);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (browser != null) {
            browser.sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button);
            browser.setFocus(true);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (browser != null) {
            browser.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (browser != null) {
            browser.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (browser != null) {
            browser.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), scrollY, 0);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (browser != null) {
            browser.sendKeyPress(keyCode, scanCode, modifiers);
            browser.setFocus(true);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (browser != null) {
            browser.sendKeyRelease(keyCode, scanCode, modifiers);
            browser.setFocus(true);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (codePoint == (char) 0) return false;
        if (browser != null) {
            browser.sendKeyTyped(codePoint, modifiers);
            browser.setFocus(true);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
