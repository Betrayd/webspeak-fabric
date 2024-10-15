package com.betrayd.webspeak.fabric.events;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public interface SetGameModeEvent {

    public static final Event<SetGameModeEvent> EVENT = EventFactory.createArrayBacked(SetGameModeEvent.class,
            listeners -> (player, gameMode, oldGameMode) -> {
                for (var l : listeners) {
                    l.onSetGameMode(player, gameMode, oldGameMode);
                }
            });

    public void onSetGameMode(ServerPlayerEntity player, GameMode gameMode, @Nullable GameMode oldGameMode);
}
