package com.betrayd.webspeak.fabric.commands;

import static net.minecraft.server.command.CommandManager.*;

import com.betrayd.webspeak.fabric.MCWebSpeakPlayer;
import com.betrayd.webspeak.fabric.WebSpeakConfig;
import com.betrayd.webspeak.fabric.WebSpeakFabric;
import com.betrayd.webspeak.fabric.WebSpeakMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.betrayd.webspeak.WebSpeakPlayer;
import net.betrayd.webspeak.WebSpeakServer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WebSpeakCommand {

    private static final SimpleCommandExceptionType NOT_RUNNING = new SimpleCommandExceptionType(
            Text.literal("Web Speak is not running!"));

    private static final SimpleCommandExceptionType ALREADY_RUNNING = new SimpleCommandExceptionType(
            Text.literal("Web Speak is already running!"));
    
    private static final SimpleCommandExceptionType NOT_CONNECTED = new SimpleCommandExceptionType(
            Text.literal("Player is not connected to Web Speak!"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("webspeak").then(
            literal("start").requires(s -> s.hasPermissionLevel(2)).executes(WebSpeakCommand::startServer)
        ).then(
            literal("stop").requires(s -> s.hasPermissionLevel(2)).executes(WebSpeakCommand::stopServer)
        ).then(
            literal("join").executes(WebSpeakCommand::joinSelf).then(
                argument("player", EntityArgumentType.player())
                    .requires(s -> s.hasPermissionLevel(2)).executes(WebSpeakCommand::joinOther)
            )
        ).then(
            literal("leave").executes(WebSpeakCommand::leaveSelf).then(
                argument("player", EntityArgumentType.player())
                    .requires(s -> s.hasPermissionLevel(2)).executes(WebSpeakCommand::leaveOther)
            )
        ).then(
            literal("status").executes(WebSpeakCommand::getSelfStatus).then(
                argument("player", EntityArgumentType.player())
                    .requires(s -> s.hasPermissionLevel(2)).executes(WebSpeakCommand::getOtherStatus)
            )
        ));
        
    }

    private static int startServer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WebSpeakFabric ws = WebSpeakFabric.get(context.getSource().getServer());
        if (ws.isRunning()) {
            throw ALREADY_RUNNING.create();
        }
        context.getSource().sendFeedback(() -> Text.literal("Starting Web Speak..."), true);
        try {
            ws.start();
        } catch (Exception e) {
            WebSpeakMod.LOGGER.error("Error starting Web Speak!", e);
            context.getSource().sendError(Text.literal("Error starting Web Speak server. See console for details..."));
            return 0;
        }
        return 1;
    }

    private static int stopServer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WebSpeakFabric ws = WebSpeakFabric.get(context.getSource().getServer());
        if (!ws.isRunning()) {
            throw NOT_RUNNING.create();
        }

        context.getSource().sendFeedback(() -> Text.literal("Stopping WebSpeak..."), true);
        ws.stop().handle((v, e) -> {
            if (e != null) {
                WebSpeakMod.LOGGER.error("Error stopping Web Speak!", e);
                context.getSource().sendError(Text.literal("Error stopping Web Speak server. See console for details..."));
            } else {
                context.getSource().sendFeedback(() -> Text.literal("Web Speak stopped."), true);
            }

            return null;
        });
        return 1;
    }

    private static int joinSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return join(context, context.getSource().getPlayerOrThrow());
    }

    private static int joinOther(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return join(context, EntityArgumentType.getPlayer(context, "player"));
    }
    
    private static int join(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        WebSpeakFabric ws = WebSpeakFabric.get(context.getSource().getServer());
        if (!ws.isRunning()) {
            throw NOT_RUNNING.create();
        }

        WebSpeakServer server = ws.getWebSpeakServer();

        WebSpeakPlayer webPlayer = server.getOrCreatePlayer(player.getUuidAsString(),
                MCWebSpeakPlayer.factory(player.networkHandler));
        webPlayer.setChannel(server.getDefaultChannel());

        WebSpeakConfig config = WebSpeakMod.getConfig();
        String url = webPlayer.getConnectionURL(config.getFrontendURL(), config.getBackendURL());

        context.getSource().sendFeedback(() -> {
            return Text.literal("Connected to Web Speak.").append(" Click ").append(Text.literal("here").setStyle(
                    Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                            .withColor(Formatting.GREEN)))
                    .append(" to join.");

        }, false);

        return 1;
    }

    private static int leaveOther(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return leave(context, EntityArgumentType.getPlayer(context, "player"));
    }

    private static int leaveSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return leave(context, context.getSource().getPlayerOrThrow());
    }

    private static int leave(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        WebSpeakFabric ws = WebSpeakFabric.get(context.getSource().getServer());
        if (!ws.isRunning()) {
            throw NOT_RUNNING.create();
        }

        if (ws.getWebSpeakServer().removePlayer(player.getUuidAsString()) == null) {
            throw NOT_CONNECTED.create();
        };

        context.getSource().sendFeedback(
                () -> Text.literal("Removed ")
                        .append(player.getStyledDisplayName())
                        .append(" from Web Speak"),
                false);
        return 1;
    }

    private static int getOtherStatus(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return getStatus(context, EntityArgumentType.getPlayer(context, "player"));
    }

    private static int getSelfStatus(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return getStatus(context, context.getSource().getPlayerOrThrow());
    }

    private static int getStatus(CommandContext<ServerCommandSource> context, ServerPlayerEntity player)
            throws CommandSyntaxException {
        WebSpeakFabric ws = WebSpeakFabric.get(context.getSource().getServer());
        if (!ws.isRunning()) {
            throw NOT_RUNNING.create();
        }

        WebSpeakPlayer wsPlayer = ws.getWebSpeakServer().getPlayer(player.getUuidAsString());
        if (wsPlayer == null) {
            context.getSource().sendFeedback(() -> Text.empty().append(player.getStyledDisplayName())
                    .append(" is not connected.").formatted(Formatting.RED), false);
            return 0;

        } else if (wsPlayer.getWsContext() != null) {
            context.getSource().sendFeedback(() -> Text.empty().append(player.getStyledDisplayName())
                    .append(" is connected from " + wsPlayer.getWsContext().session.getRemoteAddress())
                    .formatted(Formatting.GREEN), false);
            return 2;

        } else {
            context.getSource().sendFeedback(() -> Text.empty().append(player.getStyledDisplayName())
                    .append(" is connected to Web Speak, but no client is connected.").formatted(Formatting.YELLOW),
                    false);
            return 1;
        }

    }
}
