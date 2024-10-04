package com.betrayd.webspeak.fabric.util;

import com.betrayd.webspeak.fabric.WebSpeakMod;

import net.betrayd.webspeak.util.WebSpeakVector;
import net.minecraft.util.math.Vec3d;

public class WebSpeakUtils {
    public static WebSpeakVector convertVector(Vec3d vector) {
        return new WebSpeakVector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static WebSpeakVector fixCoordinateSpace(double x, double y, double z) {
        return new WebSpeakVector(-x, z, y);
    }
    
    public static String getPlayerConnectionAddress(String sessionID) {
        var config = WebSpeakMod.getConfig();
        return config.getFrontendURL() + "?server="
                + URIComponent.decode(config.getBackendURL())
                + "&id=" + sessionID;
    }
}
