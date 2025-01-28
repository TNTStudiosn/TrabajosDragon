package org.TNTStudios.trabajosdragon.trabajos;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.trabajosdragon.PagoManager;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventoMinero {
    private static final Set<UUID> jugadoresSinPago = new CopyOnWriteArraySet<>();

    public static void registrar() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (!TrabajoManager.tieneTrabajo(serverPlayer, "Minero")) return;

                int pago = obtenerPagoPorMineral(state.getBlock());

                if (pago > 0) {
                    if (LimitePagoDiario.agregarPago(serverPlayer, pago)) {
                        PagoManager.pagarJugador(serverPlayer, pago);
                    } else if (jugadoresSinPago.add(serverPlayer.getUuid())) {
                        // Solo enviamos el mensaje la primera vez que llega al límite
                        serverPlayer.sendMessage(Text.literal("⚠ Has alcanzado el límite de pago diario.")
                                .formatted(Formatting.RED), false);
                    }
                }
            }
        });
    }

    /**
     * Obtiene la cantidad de pago correspondiente a cada mineral minado.
     */
    private static int obtenerPagoPorMineral(Block block) {
        if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
            return 2;
        } else if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
            return 4;
        } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
            return 6;
        } else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
            return 10;
        } else if (block == Blocks.ANCIENT_DEBRIS) {
            return 20;
        }
        return 0;
    }
}
