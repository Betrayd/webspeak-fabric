package com.betrayd.webspeak.fabric;

import net.betrayd.webspeak.WebSpeakPlayer;
import net.betrayd.webspeak.WebSpeakServer;
import net.betrayd.webspeak.WebSpeakServer.WebSpeakPlayerFactory;
import net.betrayd.webspeak.util.WebSpeakVector;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class MCWebSpeakPlayer extends WebSpeakPlayer {

    public static final WebSpeakPlayerFactory<MCWebSpeakPlayer> factory(ServerPlayNetworkHandler netHandler) {
        return (server, playerID, sessionID) -> new MCWebSpeakPlayer(server, netHandler, sessionID);
    }

    // The network handler is the closest thing we have to a persistent "player controller" that will follow the player between respawns.
    private final ServerPlayNetworkHandler netHandler;

    public MCWebSpeakPlayer(WebSpeakServer server, ServerPlayNetworkHandler netHandler, String sessionId) {
        super(server, netHandler.player.getUuidAsString(), sessionId);
        this.netHandler = netHandler;
    }

    public ServerPlayerEntity getMcPlayer() {
        return netHandler.player;
    }

    @Override
    public WebSpeakVector getLocation() {
        // Use eye position because that's where player is speaking & listening from
        Vec3d eyePos = netHandler.player.getEyePos();
        return new WebSpeakVector(eyePos.getX(), eyePos.getY(), eyePos.getZ());
    }

    @Override
    public WebSpeakVector getForward() {
        var vec = Vec3d.fromPolar(netHandler.player.getPitch(), netHandler.player.getYaw());
        return new WebSpeakVector(vec.x, vec.y, vec.z);
        // forwardVector.set(0, 1, 0);
        // forwardVector.rotateX(mcPlayer.getPitch() * MathHelper.RADIANS_PER_DEGREE);
        // forwardVector.rotateZ(mcPlayer.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        // // LoggerFactory.getLogger(getClass()).info("Vec: {}", forwardVector);
        // return new WebSpeakVector(forwardVector.x, forwardVector.y, forwardVector.z);
    }

    @Override
    public WebSpeakVector getUp() {
        var vec = Vec3d.fromPolar(netHandler.player.getPitch() - 90, netHandler.player.getYaw());
        return new WebSpeakVector(vec.x, vec.y, vec.z);
    }

    @Override
    public boolean isInScope(WebSpeakPlayer other) {
        if (other instanceof MCWebSpeakPlayer otherMC) {
            return netHandler.player.getEyePos().isInRange(otherMC.netHandler.player.getEyePos(), 24)
                    && netHandler.player.getWorld().equals(otherMC.netHandler.player.getWorld());
        } else {
            return false;
        }
    }

}
