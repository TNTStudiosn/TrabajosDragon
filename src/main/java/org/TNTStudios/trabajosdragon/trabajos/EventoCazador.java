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
            // Enemigos básicos ($2)
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.DROWNED,
            EntityType.HUSK,
            EntityType.STRAY,

            // Enemigos moderados ($4)
            EntityType.CREEPER,
            EntityType.SLIME,
            EntityType.MAGMA_CUBE,
            EntityType.PILLAGER,

            // Enemigos avanzados ($8)
            EntityType.ENDERMAN,
            EntityType.WITCH,
            EntityType.VINDICATOR,
            EntityType.EVOKER,
            EntityType.ILLUSIONER,
            EntityType.RAVAGER,

            // Enemigos del Nether y del océano ($12)
            EntityType.BLAZE,
            EntityType.GHAST,
            EntityType.GUARDIAN,
            EntityType.PIGLIN_BRUTE,

            // Jefes intermedios ($100)
            EntityType.ELDER_GUARDIAN,
            EntityType.WARDEN,

            // Jefes finales ($500)
            EntityType.WITHER,
            EntityType.ENDER_DRAGON
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

        // Recompensas basadas en la dificultad de los enemigos
        if (tipo == EntityType.ZOMBIE || tipo == EntityType.SKELETON || tipo == EntityType.SPIDER || tipo == EntityType.DROWNED || tipo == EntityType.HUSK || tipo == EntityType.STRAY) {
            return 2;
        } else if (tipo == EntityType.CREEPER || tipo == EntityType.SLIME || tipo == EntityType.MAGMA_CUBE || tipo == EntityType.PILLAGER) {
            return 4;
        } else if (tipo == EntityType.ENDERMAN || tipo == EntityType.WITCH || tipo == EntityType.VINDICATOR || tipo == EntityType.EVOKER || tipo == EntityType.ILLUSIONER || tipo == EntityType.RAVAGER) {
            return 8;
        } else if (tipo == EntityType.BLAZE || tipo == EntityType.GHAST || tipo == EntityType.GUARDIAN || tipo == EntityType.PIGLIN_BRUTE) {
            return 12;
        } else if (tipo == EntityType.ELDER_GUARDIAN || tipo == EntityType.WARDEN) {
            return 100;
        } else if (tipo == EntityType.WITHER || tipo == EntityType.ENDER_DRAGON) {
            return 500;
        }
        return 0;
    }

}
