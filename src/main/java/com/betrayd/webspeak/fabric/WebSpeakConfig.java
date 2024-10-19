package com.betrayd.webspeak.fabric;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebSpeakConfig {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private int port = 8080;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    private boolean autoStart;

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

    private float maxDistance = 26;

    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        if (maxDistance < 0) {
            throw new IllegalArgumentException("Max distance may not be negative.");
        }
        this.maxDistance = maxDistance;
    }

    private float refDistance = 1;

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
}
