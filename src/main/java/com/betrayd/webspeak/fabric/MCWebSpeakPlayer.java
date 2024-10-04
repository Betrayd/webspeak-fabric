package com.betrayd.webspeak.fabric;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.slf4j.LoggerFactory;

import com.betrayd.webspeak.fabric.util.WebSpeakUtils;

import net.betrayd.webspeak.WebSpeakPlayer;
import net.betrayd.webspeak.WebSpeakServer;
import net.betrayd.webspeak.util.WebSpeakVector;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MCWebSpeakPlayer extends WebSpeakPlayer {

    private ServerPlayerEntity mcPlayer;

    private Vector3f forwardVector = new Vector3f();

    public MCWebSpeakPlayer(WebSpeakServer server, ServerPlayerEntity player, String sessionId) {
        super(server, player.getUuidAsString(), sessionId);
        this.mcPlayer = player;
    }

    public ServerPlayerEntity getMcPlayer() {
        return mcPlayer;
    }

    public void setMcPlayer(ServerPlayerEntity mcPlayer) {
        if (mcPlayer == null) {
            throw new NullPointerException("mcPlayer");
        }
        if (!mcPlayer.getUuidAsString().equals(getPlayerId())) {
            throw new IllegalArgumentException(
                    "Tried to asign new mc player to a webspeak player with the wrong UUID. (%s) != (%s)"
                            .formatted(mcPlayer.getUuidAsString(), getPlayerId()));
        }
        this.mcPlayer = mcPlayer;
    }

    @Override
    public WebSpeakVector getLocation() {
        // Use eye position because that's where player is speaking & listening from
        Vec3d eyePos = mcPlayer.getEyePos();
        return new WebSpeakVector(eyePos.getX(), eyePos.getY(), eyePos.getZ());
    }

    @Override
    public WebSpeakVector getForward() {
        var vec = Vec3d.fromPolar(mcPlayer.getPitch(), mcPlayer.getYaw());
        return new WebSpeakVector(vec.x, vec.y, vec.z);
        // forwardVector.set(0, 1, 0);
        // forwardVector.rotateX(mcPlayer.getPitch() * MathHelper.RADIANS_PER_DEGREE);
        // forwardVector.rotateZ(mcPlayer.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        // // LoggerFactory.getLogger(getClass()).info("Vec: {}", forwardVector);
        // return new WebSpeakVector(forwardVector.x, forwardVector.y, forwardVector.z);
    }

    @Override
    public boolean isInScope(WebSpeakPlayer other) {
        if (other instanceof MCWebSpeakPlayer otherMC) {
            return mcPlayer.getEyePos().isInRange(otherMC.mcPlayer.getEyePos(), 24)
                    && mcPlayer.getWorld().equals(otherMC.mcPlayer.getWorld());
        } else {
            return false;
        }
    }

}
