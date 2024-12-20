package com.betrayd.webspeak.fabric;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.betrayd.webspeak.WebSpeakGroup;
import net.betrayd.webspeak.WebSpeakPlayer;
import net.betrayd.webspeak.WebSpeakServer;
import net.betrayd.webspeak.util.AudioModifier;
import net.betrayd.webspeak.util.PannerOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;

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

    private WebSpeakGroup survivalGroup;

    public WebSpeakGroup getSurvivalGroup() {
        return survivalGroup;
    }

    private WebSpeakGroup spectatorGroup;

    public WebSpeakGroup getSpectatorGroup() {
        return spectatorGroup;
    }

    private WebSpeakGroup deadGroup;

    public WebSpeakGroup getDeadGroup() {
        return deadGroup;
    }

    public boolean isRunning() {
        return webSpeakServer != null;
    }

    public void start() {
        if (webSpeakServer != null) {
            throw new IllegalStateException("Server is already running.");
        }
        
        webSpeakServer = new WebSpeakServer();
        // webSpeakServer.getPannerOptions().maxDistance = config.getMaxRange();
        updatePannerOptions();

        webSpeakServer.onPlayerAdded(p -> {
            if (p instanceof MCWebSpeakPlayer webPlayer) {
                recalcGamemode(webPlayer, webPlayer.getMcPlayer().interactionManager.getGameMode());
            }
        });

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

        try {
            WebSpeakConfig config = WebSpeakMod.getConfig();
            if(config.isUseRelay()) {
                webSpeakServer.startRelay(config.getRelayAddress());
            }
            else {
                webSpeakServer.startJetty(config.getPort());
            }
        } catch (Exception e) {
            WebSpeakMod.LOGGER.error("Error launching WebSpeak", e);
            return;
        }

        survivalGroup = new WebSpeakGroup("survival");
        spectatorGroup = new WebSpeakGroup("spectator");
        deadGroup = new WebSpeakGroup("dead");

        survivalGroup.setAudioModifier(spectatorGroup, new AudioModifier(true, null));
        survivalGroup.setAudioModifier(deadGroup, new AudioModifier(true, null));

        spectatorGroup.setAudioModifier(spectatorGroup, new AudioModifier(null, false));
        spectatorGroup.setAudioModifier(deadGroup, new AudioModifier(true, null));
    }

    /**
     * Update the server's panner options from the mod config.
     */
    public void updatePannerOptions() {
        WebSpeakConfig config = WebSpeakMod.getConfig();
        PannerOptions panner = webSpeakServer.getPannerOptions();
        if (webSpeakServer != null) {
            panner.maxDistance = config.getMaxDistance();
            panner.refDistance = config.getRefDistance();
            panner.rolloffFactor = config.getRolloffFactor();
            webSpeakServer.updatePannerOptions();
        }
    }
    
    public void onGamemodeChange(ServerPlayerEntity player, GameMode newGamemode) {
        MCWebSpeakPlayer webPlayer = getPlayer(player);
        if (webPlayer == null)
            return;
        
        recalcGamemode(webPlayer, newGamemode);
    }

    private void recalcGamemode(WebSpeakPlayer player, GameMode gamemode) {
        if (gamemode == GameMode.SPECTATOR) {
            player.removeGroup(survivalGroup);
            player.addGroup(spectatorGroup);
        } else {
            player.removeGroup(spectatorGroup);
            player.addGroup(survivalGroup);
        }
    }

    public void onPlayerDied(ServerPlayerEntity player) {
        MCWebSpeakPlayer webPlayer = getPlayer(player);
        if (webPlayer != null) {
            webPlayer.addGroup(deadGroup);
        }
    }

    public void onPlayerRespawned(ServerPlayerEntity player) {
        MCWebSpeakPlayer webPlayer = getPlayer(player);
        if (webPlayer != null) {
            webPlayer.removeGroup(deadGroup);
        }
    }

    public MCWebSpeakPlayer getPlayer(UUID playerUUID) {
        if (webSpeakServer != null) {
            return (MCWebSpeakPlayer) webSpeakServer.getPlayer(playerUUID.toString());
        } else {
            return null;
        }
    }

    public MCWebSpeakPlayer getPlayer(PlayerEntity player) {
        return getPlayer(player.getUuid());
    }

    public void tick() {
        if (webSpeakServer != null && webSpeakServer.isRunning()) {
            webSpeakServer.tick();
        }
    }

    private CompletableFuture<Void> stopFuture;

    public CompletableFuture<Void> stop() {
        if (stopFuture != null) {
            return stopFuture;
        }

        stopFuture = CompletableFuture.runAsync(() -> {
            if (webSpeakServer != null && webSpeakServer.isRunning()) {
                
            }

            webSpeakServer = null;
            stopFuture = null;
        }, Util.getMainWorkerExecutor());

        return stopFuture;
    }

    /**
     * Update the mod's config with new panner options.
     * @param pannerOptions New panner options to set.
     */
    public void setPannerOptions(PannerOptions.Partial pannerOptions) {
        WebSpeakConfig config = WebSpeakMod.getConfig();
        if (pannerOptions.distanceModel != null) {
            config.setDistanceModel(pannerOptions.distanceModel);
        }
        if (pannerOptions.maxDistance != null) {
            config.setMaxDistance(pannerOptions.maxDistance);
        }
        if (pannerOptions.refDistance != null) {
            config.setRefDistance(pannerOptions.refDistance);
        }
        if (pannerOptions.rolloffFactor != null) {
            config.setRolloffFactor(pannerOptions.rolloffFactor);
        }
        config.saveAsync();
        if (webSpeakServer != null) {
            webSpeakServer.getPannerOptions().copyFrom(pannerOptions);
            webSpeakServer.updatePannerOptions();
        }
    }
    
}
