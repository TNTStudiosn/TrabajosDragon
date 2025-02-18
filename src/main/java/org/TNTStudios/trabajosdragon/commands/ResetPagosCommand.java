package org.TNTStudios.trabajosdragon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.trabajosdragon.trabajos.*;

import java.util.HashMap;

public class ResetPagosCommand {

    public static void registrar() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("resetpagos")
                    .requires(source -> source.hasPermissionLevel(2)) // Solo operadores y consola
                    .executes(context -> {
                        ServerCommandSource source = context.getSource();

                        // Reiniciar los límites de pago de cada tipo de trabajo
                        LimitePagoDiario.setPagosDiarios(new HashMap<>());
                        LimitePagoDiarioAgricultor.setPagosDiarios(new HashMap<>());
                        LimitePagoDiarioCarnicero.setPagosDiarios(new HashMap<>());
                        LimitePagoDiarioCartografo.setPagosDiarios(new HashMap<>());
                        LimitePagoDiarioCazador.setPagosDiarios(new HashMap<>());
                        LimitePagoDiarioLenador.setPagosDiarios(new HashMap<>());
                        LimitePagoDiarioPescador.setPagosDiarios(new HashMap<>());

                        // Mensaje de confirmación
                        source.sendMessage(Text.literal("✔ Se han reiniciado todos los límites de pago diario.")
                                .formatted(Formatting.GREEN));

                        return 1;
                    })
            );
        });
    }
}
