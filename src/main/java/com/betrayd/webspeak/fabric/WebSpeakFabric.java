package com.betrayd.webspeak.fabric;

import java.util.concurrent.CompletableFuture;

import org.slf4j.LoggerFactory;

import net.betrayd.webspeak.WebSpeakServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class WebSpeakFabric {
    public static WebSpeakFabric get(MinecraftServer server) {
        return ((WebSpeakProvider) server).getWebSpeak();
    }

    private final MinecraftServer minecraftServer;
    private WebSpeakServer webSpeakServer;

    public WebSpeakFabric(MinecraftServer server) {
        this.minecraftServer = server;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    public WebSpeakServer getWebSpeakServer() {
        return webSpeakServer;
    }

    public boolean isRunning() {
        return webSpeakServer != null;
    }

    public void start() {
        if (webSpeakServer != null) {
            throw new IllegalStateException("Server is already running.");
        }
        WebSpeakConfig config = WebSpeakMod.getConfig();

        webSpeakServer = new WebSpeakServer();
        webSpeakServer.getPannerOptions().maxDistance = config.getMaxRange();

        webSpeakServer.onSessionConnected(player -> {
            if (player instanceof MCWebSpeakPlayer mcPlayer) {
                mcPlayer.getMcPlayer().sendMessage(Text.literal("WebSpeak client connected."));
            }
        });

        webSpeakServer.onSessionDisconnected(player -> {
            if (player instanceof MCWebSpeakPlayer mcPlayer) {
                mcPlayer.getMcPlayer().sendMessage(Text.literal("WebSpeak client disconnected."));
            }
        });

        webSpeakServer.start(config.getPort());
    }

    public void tick() {
        if (webSpeakServer != null && webSpeakServer.getApp() != null) {
            webSpeakServer.tick();
        }
    }

    private CompletableFuture<Void> stopFuture;

    public CompletableFuture<Void> stop() {
        if (stopFuture != null) {
            return stopFuture;
        }

        stopFuture = CompletableFuture.runAsync(() -> {
            if (webSpeakServer != null && webSpeakServer.getApp() != null) {
                LoggerFactory.getLogger(getClass()).info("Shutting down WebSpeak");
                webSpeakServer.stop();
            }

            webSpeakServer = null;
            stopFuture = null;
        }, Util.getMainWorkerExecutor());

        return stopFuture;
    }
}
