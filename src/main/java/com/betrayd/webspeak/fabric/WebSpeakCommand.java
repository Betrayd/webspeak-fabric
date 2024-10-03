package com.betrayd.webspeak.fabric;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class WebSpeakCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("webspeak").then(
            literal("start").executes(ctx -> {
                ctx.getSource().sendFeedback(() -> Text.literal("Starting WebSpeak..."), true);
                WebSpeakFabric.get(ctx.getSource().getServer()).start();
                return 1;
            })
        ));
    }
    
}
