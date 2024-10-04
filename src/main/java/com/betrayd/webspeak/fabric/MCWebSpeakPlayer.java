package com.betrayd.webspeak.fabric;

import net.betrayd.webspeak.WebSpeakPlayer;
import net.betrayd.webspeak.WebSpeakServer;
import net.betrayd.webspeak.util.WebSpeakVector;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class MCWebSpeakPlayer extends WebSpeakPlayer {

    private ServerPlayerEntity mcPlayer;

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
        return new WebSpeakVector(-eyePos.getX(), eyePos.getZ(), eyePos.getY());
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
