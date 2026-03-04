package com.galaxianexus.browser;

import com.galaxianexus.browser.config.BrowserConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalaxiaNexusBrowserMod implements ClientModInitializer {
    public static final String MOD_ID = "galaxianexus-browser";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static GalaxiaNexusBrowserMod instance;
    private BrowserManager browserManager;
    private BrowserConfig config;

    @Override
    public void onInitializeClient() {
        instance = this;
        config = BrowserConfig.load();
        browserManager = new BrowserManager(config);
        
        LOGGER.info("GalaxiaNexus Browser Mod initialized! Mode: {}", config.getMode());
        
        registerCommands();
    }
    
    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("browser")
                .executes(context -> {
                    browserManager.openBrowser(config.getDefaultUrl());
                    return 1;
                })
                .then(ClientCommandManager.argument("url", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                    .executes(context -> {
                        String url = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "url");
                        browserManager.openBrowser(url);
                        return 1;
                    })
                )
            );
        });
    }
    
    public static GalaxiaNexusBrowserMod getInstance() {
        return instance;
    }
    
    public BrowserManager getBrowserManager() {
        return browserManager;
    }
    
    public BrowserConfig getConfig() {
        return config;
    }
}
