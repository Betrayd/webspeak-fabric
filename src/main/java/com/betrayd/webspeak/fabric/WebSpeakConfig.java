package com.betrayd.webspeak.fabric;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebSpeakConfig {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private float maxRange = 24;

    public float getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

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
