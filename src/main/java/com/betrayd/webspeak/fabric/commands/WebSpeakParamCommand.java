package com.betrayd.webspeak.fabric.commands;
import static net.minecraft.server.command.CommandManager.*;

import com.betrayd.webspeak.fabric.WebSpeakFabric;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.betrayd.webspeak.util.PannerOptions;
import net.betrayd.webspeak.util.PannerOptions.DistanceModelType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class WebSpeakParamCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("webspeak").then(
            literal("setparam").requires(s -> s.hasPermissionLevel(2)).then(
                literal("distanceModel").then(
                    literal("linear").executes(ctx -> setDistanceModel(ctx, DistanceModelType.LINEAR))
                ).then(
                    literal("inverse").executes(ctx -> setDistanceModel(ctx, DistanceModelType.INVERSE))
                ).then(
                    literal("exponential").executes(ctx -> setDistanceModel(ctx, DistanceModelType.EXPONENTIAL))
                )
            ).then(
                literal("maxDistance").then(
                    argument("value", FloatArgumentType.floatArg(0)).executes(WebSpeakParamCommand::setMaxDistance)
                )
            ).then(
                literal("refDistance").then(
                    argument("value", FloatArgumentType.floatArg(0)).executes(WebSpeakParamCommand::setRefDistance)
                )
            ).then(
                literal("rolloffFactor").then(
                    argument("value", FloatArgumentType.floatArg(1)).executes(WebSpeakParamCommand::setRolloffFactor)
                )
            )
        ));
    }
    
    private static int setDistanceModel(CommandContext<ServerCommandSource> context, DistanceModelType distanceModel) {
        var partial = new PannerOptions.Partial();
        partial.distanceModel = distanceModel;
        WebSpeakFabric.get(context.getSource().getServer()).setPannerOptions(partial);
        context.getSource().sendFeedback(() -> Text.literal("Set WebSpeak distance model to " + distanceModel.name()), true);
        return 1;
    }

    private static int setMaxDistance(CommandContext<ServerCommandSource> context) {
        var maxDistance = FloatArgumentType.getFloat(context, "value");
        var partial = new PannerOptions.Partial();
        partial.maxDistance = maxDistance;
        WebSpeakFabric.get(context.getSource().getServer()).setPannerOptions(partial);
        context.getSource().sendFeedback(() -> Text.literal("Set WebSpeak maxDistance to " + maxDistance), true);
        return 1;
    }

    private static int setRefDistance(CommandContext<ServerCommandSource> context) {
        var refDistance = FloatArgumentType.getFloat(context, "value");
        var partial = new PannerOptions.Partial();
        partial.refDistance = refDistance;
        WebSpeakFabric.get(context.getSource().getServer()).setPannerOptions(partial);
        context.getSource().sendFeedback(() -> Text.literal("Set WebSpeak refDistance to " + refDistance), true);
        return 1;
    }

    private static int setRolloffFactor(CommandContext<ServerCommandSource> context) {
        var rolloffFactor = FloatArgumentType.getFloat(context, "value");
        var partial = new PannerOptions.Partial();
        partial.rolloffFactor = rolloffFactor;
        WebSpeakFabric.get(context.getSource().getServer()).setPannerOptions(partial);
        context.getSource().sendFeedback(() -> Text.literal("Set WebSpeak rolloffFactor to " + rolloffFactor), true);
        return 1;
    }
}
