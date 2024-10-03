package com.betrayd.webspeak.fabric.mixin;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.betrayd.webspeak.fabric.WebSpeakFabric;
import com.betrayd.webspeak.fabric.WebSpeakProvider;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profiler;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements WebSpeakProvider {

    @Unique
    private WebSpeakFabric webSpeak;

    @Shadow
    private Profiler profiler;

    @Override
    public WebSpeakFabric getWebSpeak() {
        if (webSpeak == null) {
            webSpeak = new WebSpeakFabric((MinecraftServer) (Object) this);
        }
        return webSpeak;
    }

    @Override
    public WebSpeakFabric getWebSpeakIfPresent() {
        return webSpeak;
    }
    
    @Inject(method = "tick", at=@At("TAIL"))
    void betrayd$onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (webSpeak != null) {
            profiler.push("webspeak");
            webSpeak.tick();
            profiler.pop();
        }
    }

    @Unique
    private CompletableFuture<Void> webSpeakShutdownFuture;

    @Inject(method = "shutdown", at=@At("HEAD"))
    void betrayd$onShutdown(CallbackInfo ci) {
        if (webSpeak != null) {
            webSpeakShutdownFuture = webSpeak.stop();
        }
    }

    @Inject(method = "shutdown", at=@At("TAIL"))
    void betrayd$onEndShutdown(CallbackInfo ci) {
        if (webSpeakShutdownFuture != null) {
            webSpeakShutdownFuture.join();
        }
    }
}