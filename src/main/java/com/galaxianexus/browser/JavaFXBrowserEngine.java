package com.galaxianexus.browser;

import com.galaxianexus.browser.config.BrowserConfig;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * JavaFX WebView-based browser engine with full JavaScript and AJAX support
 */
public class JavaFXBrowserEngine {
    private static boolean javaFXInitialized = false;
    private static final Object initLock = new Object();
    
    private WebView webView;
    private WebEngine webEngine;
    private BrowserConfig config;
    private BufferedImage currentSnapshot;
    private boolean isLoading = false;
    
    public JavaFXBrowserEngine(BrowserConfig config) {
        this.config = config;
        ensureJavaFXInitialized();
    }
    
    private static void ensureJavaFXInitialized() {
        synchronized (initLock) {
            if (!javaFXInitialized) {
                CountDownLatch latch = new CountDownLatch(1);
                
                Platform.startup(() -> {
                    GalaxiaNexusBrowserMod.LOGGER.info("JavaFX Platform initialized for browser");
                    latch.countDown();
                });
                
                try {
                    latch.await();
                    javaFXInitialized = true;
                } catch (InterruptedException e) {
                    GalaxiaNexusBrowserMod.LOGGER.error("Failed to initialize JavaFX", e);
                }
            }
        }
    }
    
    public void initialize() {
        Platform.runLater(() -> {
            webView = new WebView();
            webEngine = webView.getEngine();
            
            // Configure WebEngine
            webEngine.setJavaScriptEnabled(config.isEnableJavaScript());
            webEngine.setUserAgent("Mozilla/5.0 (GalaxiaNexus Browser) AppleWebKit/537.36 KHTML Gecko Chrome Safari");
            
            // Set dimensions
            webView.setPrefSize(config.getBrowserWidth(), config.getBrowserHeight());
            webView.setMinSize(config.getBrowserWidth(), config.getBrowserHeight());
            webView.setMaxSize(config.getBrowserWidth(), config.getBrowserHeight());
            
            // Add state change listeners
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                isLoading = newState == javafx.concurrent.Worker.State.RUNNING;
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    GalaxiaNexusBrowserMod.LOGGER.info("Page loaded successfully");
                    captureSnapshot();
                } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                    GalaxiaNexusBrowserMod.LOGGER.error("Page load failed: {}", 
                        webEngine.getLoadWorker().getException());
                }
            });
            
            GalaxiaNexusBrowserMod.LOGGER.info("JavaFX WebView initialized");
        });
    }
    
    public void loadUrl(String url) {
        Platform.runLater(() -> {
            isLoading = true;
            webEngine.load(url);
            GalaxiaNexusBrowserMod.LOGGER.info("Loading URL: {}", url);
        });
    }
    
    public void executeScript(String script) {
        if (!config.isEnableJavaScript()) {
            GalaxiaNexusBrowserMod.LOGGER.warn("JavaScript is disabled in config");
            return;
        }
        
        Platform.runLater(() -> {
            try {
                Object result = webEngine.executeScript(script);
                GalaxiaNexusBrowserMod.LOGGER.debug("Script executed: {}", result);
            } catch (Exception e) {
                GalaxiaNexusBrowserMod.LOGGER.error("Script execution failed", e);
            }
        });
    }
    
    public void reload() {
        Platform.runLater(() -> webEngine.reload());
    }
    
    public void goBack() {
        Platform.runLater(() -> {
            if (webEngine.getHistory().getCurrentIndex() > 0) {
                webEngine.getHistory().go(-1);
            }
        });
    }
    
    public void goForward() {
        Platform.runLater(() -> {
            if (webEngine.getHistory().getCurrentIndex() < webEngine.getHistory().getEntries().size() - 1) {
                webEngine.getHistory().go(1);
            }
        });
    }
    
    private void captureSnapshot() {
        Platform.runLater(() -> {
            try {
                WritableImage fxImage = webView.snapshot(new SnapshotParameters(), null);
                currentSnapshot = SwingFXUtils.fromFXImage(fxImage, null);
                GalaxiaNexusBrowserMod.LOGGER.debug("Captured webpage snapshot");
            } catch (Exception e) {
                GalaxiaNexusBrowserMod.LOGGER.error("Failed to capture snapshot", e);
            }
        });
    }
    
    public BufferedImage getCurrentSnapshot() {
        return currentSnapshot;
    }
    
    public boolean isLoading() {
        return isLoading;
    }
    
    public String getCurrentUrl() {
        CompletableFuture<String> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                future.complete(webEngine.getLocation());
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        try {
            return future.get();
        } catch (Exception e) {
            return "";
        }
    }
    
    public void cleanup() {
        Platform.runLater(() -> {
            if (webView != null) {
                webView = null;
                webEngine = null;
            }
        });
    }
}
