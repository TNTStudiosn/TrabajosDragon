package org.TNTStudios.trabajosdragon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.trabajosdragon.trabajos.TrabajoManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TrabajoCommand {
    private static final List<String> TRABAJOS_DISPONIBLES = Arrays.asList("Minero", "Lenador", "Cazador", "Agricultor", "Pescador", "Arquitecto", "Cartografo", "Constructor", "Explorador", "Cazador", "Decorador", "RedStoner", "Granjero", "Repartidor", "Policia", "Medico");

    public static void registrar() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("trabajo")
                    .then(CommandManager.argument("nombre", StringArgumentType.string())
                            .suggests(TrabajoCommand::sugerirTrabajos) // Autocompletado con TAB
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                if (player != null) {
                                    // Verificar si ya tiene un trabajo
                                    if (TrabajoManager.obtenerTrabajo(player) != null) {
                                        player.sendMessage(Text.of("⚠ Ya tienes un trabajo asignado. No puedes cambiarlo sin permiso de un administrador.")
                                                .copy().formatted(Formatting.RED), false);
                                        return 1;
                                    }

                                    String trabajo = StringArgumentType.getString(context, "nombre");
                                    if (TRABAJOS_DISPONIBLES.contains(trabajo)) {
                                        if (TrabajoManager.asignarTrabajo(player, trabajo)) {
                                            player.sendMessage(Text.of("✔ Ahora eres un " + trabajo + ".")
                                                    .copy().formatted(Formatting.GREEN), false);
                                        } else {
                                            player.sendMessage(Text.of("⚠ No se pudo asignar el trabajo.")
                                                    .copy().formatted(Formatting.RED), false);
                                        }
                                    } else {
                                        player.sendMessage(Text.of("⚠ Trabajo no reconocido.")
                                                .copy().formatted(Formatting.RED), false);
                                    }
                                }
                                return 1;
                            })
                    )
                    .then(CommandManager.literal("remover")
                            .requires(source -> source.hasPermissionLevel(4)) // Solo operadores
                            .then(CommandManager.argument("jugador", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        context.getSource().getServer().getPlayerManager().getPlayerList().forEach(player ->
                                                builder.suggest(player.getEntityName())
                                        );
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> {
                                        ServerPlayerEntity admin = context.getSource().getPlayer();
                                        String playerName = StringArgumentType.getString(context, "jugador");

                                        ServerPlayerEntity targetPlayer = context.getSource().getServer()
                                                .getPlayerManager().getPlayer(playerName);

                                        if (targetPlayer != null) {
                                            if (TrabajoManager.removerTrabajo(targetPlayer)) {
                                                context.getSource().sendMessage(Text.of("✔ Se ha eliminado el trabajo de " + playerName)
                                                        .copy().formatted(Formatting.GREEN));
                                                targetPlayer.sendMessage(Text.of("⚠ Un administrador ha eliminado tu trabajo. Ahora puedes elegir otro.")
                                                        .copy().formatted(Formatting.YELLOW), false);
                                            } else {
                                                context.getSource().sendMessage(Text.of("⚠ El jugador no tenía un trabajo asignado.")
                                                        .copy().formatted(Formatting.RED));
                                            }
                                        } else {
                                            context.getSource().sendMessage(Text.of("⚠ Jugador no encontrado.")
                                                    .copy().formatted(Formatting.RED));
                                        }
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }

    /**
     * Autocompletado de los trabajos disponibles cuando el jugador presiona TAB.
     */
    private static CompletableFuture<Suggestions> sugerirTrabajos(com.mojang.brigadier.context.CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        TRABAJOS_DISPONIBLES.forEach(builder::suggest);
        return builder.buildFuture();
    }
}
