package com.betrayd.webspeak.fabric;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betrayd.webspeak.fabric.commands.WebSpeakCommand;
import com.betrayd.webspeak.fabric.commands.WebSpeakParamCommand;
import com.betrayd.webspeak.fabric.events.SetGameModeEvent;

import net.betrayd.webspeak.WebSpeakServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class WebSpeakMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("webspeak-fabric");

    private static WebSpeakConfig config;

    public static WebSpeakConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        loadConfig();
        CommandRegistrationCallback.EVENT.register(WebSpeakCommand::register);
        CommandRegistrationCallback.EVENT.register(WebSpeakParamCommand::register);

        ServerPlayConnectionEvents.DISCONNECT.register((netHandler, server) -> {
            WebSpeakServer webSpeak = WebSpeakFabric.get(server).getWebSpeakServer();
            webSpeak.removePlayer(netHandler.player.getUuidAsString());
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (config.autoStart())
                WebSpeakFabric.get(server).start();
        });

        SetGameModeEvent.EVENT.register((player, newGamemode, oldGamemode) -> {
            WebSpeakFabric ws = WebSpeakFabric.get(player.getServer());
            ws.onGamemodeChange(player, newGamemode);
        });
    }

    private static void loadConfig() {
        Path configFile = WebSpeakConfig.CONFIG_FILE;
        if (Files.exists(configFile)) {
            try(BufferedReader reader = Files.newBufferedReader(configFile)) {
                config = WebSpeakConfig.fromJson(reader);
            } catch (Exception e) {
                LOGGER.error("Error loading WebSpeak config file. Default values will be used for this session.", e);
                config = new WebSpeakConfig();
            }
        } else {
            config = new WebSpeakConfig();
        }

        // Immedietly save config to file to update any fields that may have changed.
        config.saveAsync();
    }
}