package org.TNTStudios.trabajosdragon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.trabajosdragon.trabajos.TrabajoManager;

public class TrabajoCommand {
    public static void registrar() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("trabajo")
                    .then(CommandManager.argument("nombre", StringArgumentType.string())
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                if (player != null) {
                                    String trabajo = StringArgumentType.getString(context, "nombre");
                                    if (trabajo.equalsIgnoreCase("Minero")) {
                                        TrabajoManager.asignarTrabajo(player, trabajo);
                                        player.sendMessage(Text.literal("✔ Ahora eres un Minero.")
                                                .formatted(Formatting.GREEN), false);
                                    } else {
                                        player.sendMessage(Text.literal("⚠ Trabajo no reconocido.")
                                                .formatted(Formatting.RED), false);
                                    }
                                }
                                return 1;
                            })
                    )
            );
        });
    }
}
