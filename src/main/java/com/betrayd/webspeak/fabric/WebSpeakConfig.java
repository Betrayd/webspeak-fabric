package com.betrayd.webspeak.fabric;

import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.betrayd.webspeak.util.PannerOptions.DistanceModelType;
import net.fabricmc.loader.api.FabricLoader;

public class WebSpeakConfig {
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("webspeak.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private int port = 8080;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private boolean useRelay = true;

    public boolean isUseRelay()
    {
        return useRelay;
    }

    public void setUseRelay(boolean useRelay) {
        this.useRelay = useRelay;
    }

    private String relayAddress = "wss://webspeak.betrayd.net";

    public String getRelayAddress()
    {
        return relayAddress;
    }

    public void setRelayAddress(String relayAddress) {
        this.relayAddress = relayAddress;
    }

    private String frontendURL = "https://betrayd.github.io/web-speak";

    public String getFrontendURL() {
        return frontendURL;
    }

    public void setFrontendURL(String frontendURL) {
        this.frontendURL = frontendURL;
    }

    private String backendURL = "http://localhost:8080";

    public String getBackendURL() {
        return backendURL;
    }

    public void setBackendURL(String backendURL) {
        this.backendURL = backendURL;
    }

    private boolean autoStart = true;

    public boolean autoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
    
    private float maxRange = -1;

    public float getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    private DistanceModelType distanceModel = DistanceModelType.LINEAR;

    public DistanceModelType getDistanceModel() {
        return distanceModel;
    }

    public void setDistanceModel(DistanceModelType distanceModel) {
        this.distanceModel = distanceModel;
    }

    private float maxDistance = 24;

    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        if (maxDistance < 0) {
            throw new IllegalArgumentException("Max distance may not be negative.");
        }
        this.maxDistance = maxDistance;
    }

    private float refDistance = 4;

    public float getRefDistance() {
        return refDistance;
    }

    public void setRefDistance(float refDistance) {
        if (refDistance < 0) {
            throw new IllegalArgumentException("Ref distance may not be negative.");
        }
        this.refDistance = refDistance;
    }

    private float rolloffFactor = 1;

    public float getRolloffFactor() {
        return rolloffFactor;
    }

    public void setRolloffFactor(float rolloffFactor) {
        if (rolloffFactor < 0) {
            throw new IllegalArgumentException("Rolloff factor may not be negative.");
        }
        this.rolloffFactor = rolloffFactor;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static WebSpeakConfig fromJson(String json) {
        return GSON.fromJson(json, WebSpeakConfig.class);
    }

    public static WebSpeakConfig fromJson(Reader reader) {
        return GSON.fromJson(reader, WebSpeakConfig.class);
    }
    
    /**
     * Asynchronously save the WebSpeak config to file.
     * @return A future that completes when the config is saved.
     */
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(() -> {
            try(BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
                writer.write(toJson());
            } catch (Exception e) {
                WebSpeakMod.LOGGER.error("Error saving WebSpeak config.", e);
                throw new CompletionException(e);
            }
        });
    }
}
