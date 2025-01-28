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

public class EventoLenador {
    private static final Set<UUID> jugadoresSinPago = new CopyOnWriteArraySet<>();

    public static void registrar() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (!TrabajoManager.tieneTrabajo(serverPlayer, "Lenador")) return;

                int pago = obtenerPagoPorMadera(state.getBlock());

                if (pago > 0) {
                    if (LimitePagoDiarioLenador.agregarPago(serverPlayer, pago)) {
                        PagoManager.pagarJugador(serverPlayer, pago);
                    } else if (jugadoresSinPago.add(serverPlayer.getUuid())) {
                        serverPlayer.sendMessage(Text.literal("⚠ Has alcanzado el límite de pago diario.")
                                .formatted(Formatting.RED), false);
                    }
                }
            }
        });
    }

    /**
     * Determina la cantidad de pago según el tipo de madera talada.
     */
    private static int obtenerPagoPorMadera(Block block) {
        if (block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG) {
            return 1; // Roble/Abedul
        } else if (block == Blocks.JUNGLE_LOG || block == Blocks.SPRUCE_LOG) {
            return 2; // Jungla/Pino
        } else if (block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG) {
            return 3; // Acacia/Roble oscuro
        }
        return 0;
    }
}
