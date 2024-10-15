package com.betrayd.webspeak.fabric.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.betrayd.webspeak.fabric.events.SetGameModeEvent;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "setGameMode", at = @At("RETURN"))
    void webspeak$onSetGameMode(GameMode gameMode, @Nullable GameMode previousGameMode, CallbackInfo ci) {
        SetGameModeEvent.EVENT.invoker().onSetGameMode(player, gameMode, previousGameMode);
    }
}
