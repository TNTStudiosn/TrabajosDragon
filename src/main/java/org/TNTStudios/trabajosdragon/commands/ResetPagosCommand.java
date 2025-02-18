package org.TNTStudios.trabajosdragon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.trabajosdragon.trabajos.*;

public class ResetPagosCommand {

    public static void registrar() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("resetpagos")
                    .requires(source -> source.hasPermissionLevel(2)) // Solo operadores y consola
                    .executes(context -> {
                        ServerCommandSource source = context.getSource();

                        // Reiniciar los límites de pago de TODOS los jugadores (conectados y desconectados)
                        LimitePagoDiario.resetearLimitesDiarios();
                        LimitePagoDiarioAgricultor.resetearLimitesDiarios();
                        LimitePagoDiarioCarnicero.resetearLimitesDiarios();
                        LimitePagoDiarioCartografo.resetearLimitesDiarios();
                        LimitePagoDiarioCazador.resetearLimitesDiarios();
                        LimitePagoDiarioLenador.resetearLimitesDiarios();
                        LimitePagoDiarioPescador.resetearLimitesDiarios();

                        // Enviar mensaje a todos los jugadores en línea
                        Text mensaje = Text.literal("✔ Se han reiniciado todos los límites de pago diario.")
                                .formatted(Formatting.GREEN);

                        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
                            player.sendMessage(mensaje, false);
                        }

                        // También mostrarlo en la consola del servidor
                        source.sendMessage(mensaje);

                        return 1;
                    })
            );
        });
    }
}
