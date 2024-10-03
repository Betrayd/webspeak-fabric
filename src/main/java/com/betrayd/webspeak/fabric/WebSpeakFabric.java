package com.betrayd.webspeak.fabric;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;

import net.betrayd.webspeak.WebSpeakServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;

public class WebSpeakFabric {
    public static WebSpeakFabric get(MinecraftServer server) {
        return ((WebSpeakProvider) server).getWebSpeak();
    }

    private final MinecraftServer minecraftServer;
    private final WebSpeakServer webSpeakServer = new WebSpeakServer();

    public WebSpeakFabric(MinecraftServer server) {
        this.minecraftServer = server;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public WebSpeakServer getWebSpeakServer() {
        return webSpeakServer;
    }

    public void start() {
        webSpeakServer.start(8080);
    }

    public void tick() {
        if (webSpeakServer.getApp() != null) {
            webSpeakServer.tick();
        }
    }

    public CompletableFuture<Void> stop() {
        return CompletableFuture.runAsync(() -> {
            if (webSpeakServer != null && webSpeakServer.getApp() != null) {
                LoggerFactory.getLogger(getClass()).info("Shutting down WebSpeak");
                webSpeakServer.stop();
            }
        }, Util.getMainWorkerExecutor());
    }
}
