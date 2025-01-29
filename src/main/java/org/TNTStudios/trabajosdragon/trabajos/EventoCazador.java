package org.TNTStudios.trabajosdragon.trabajos;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.trabajosdragon.PagoManager;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventoCazador {
    private static final Map<UUID, Boolean> jugadoresSinPago = new ConcurrentHashMap<>();
    private static final Set<EntityType<?>> ENTIDADES_RECOMPENSADAS = Set.of(
            EntityType.ZOMBIE, EntityType.SKELETON, // $2
            EntityType.CREEPER, // $4
            EntityType.ENDERMAN, // $8
            EntityType.WITHER, EntityType.ENDER_DRAGON // $500
    );

    public static void registrar() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (!(entity instanceof ServerPlayerEntity serverPlayer)) return;

            // Cachear el trabajo del jugador para evitar múltiples consultas a TrabajoManager
            boolean esCazador = TrabajoManager.tieneTrabajo(serverPlayer, "Cazador");
            if (!esCazador) return;

            int pago = obtenerPagoPorMatar(killedEntity);

            if (pago > 0) {
                if (LimitePagoDiarioCazador.agregarPago(serverPlayer, pago)) {
                    PagoManager.pagarJugador(serverPlayer, pago);
                } else if (jugadoresSinPago.putIfAbsent(serverPlayer.getUuid(), true) == null) {
                    serverPlayer.sendMessage(Text.literal("⚠ Has alcanzado el límite de pago diario.")
                            .formatted(Formatting.RED), false);
                }
            }
        });
    }

    /**
     * Determina la cantidad de pago según el mob eliminado.
     */
    private static int obtenerPagoPorMatar(Entity entity) {
        EntityType<?> tipo = entity.getType();
        if (!ENTIDADES_RECOMPENSADAS.contains(tipo)) return 0;

        // Reemplazar switch con if-else para evitar errores de compilación
        if (tipo == EntityType.ZOMBIE || tipo == EntityType.SKELETON) {
            return 2;
        } else if (tipo == EntityType.CREEPER) {
            return 4;
        } else if (tipo == EntityType.ENDERMAN) {
            return 8;
        } else if (tipo == EntityType.WITHER || tipo == EntityType.ENDER_DRAGON) {
            return 500;
        }
        return 0;
    }
}
