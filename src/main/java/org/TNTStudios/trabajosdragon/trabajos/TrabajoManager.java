package org.TNTStudios.trabajosdragon.trabajos;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class TrabajoManager {
    private static final HashMap<UUID, String> trabajos = new HashMap<>();

    /**
     * Asigna un trabajo a un jugador.
     *
     * @param player Jugador que elige un trabajo.
     * @param trabajo Nombre del trabajo.
     */
    public static void asignarTrabajo(ServerPlayerEntity player, String trabajo) {
        trabajos.put(player.getUuid(), trabajo);
    }

    /**
     * Obtiene el trabajo actual de un jugador.
     *
     * @param player Jugador del cual se desea conocer el trabajo.
     * @return Nombre del trabajo asignado o null si no tiene ninguno.
     */
    public static String obtenerTrabajo(ServerPlayerEntity player) {
        return trabajos.getOrDefault(player.getUuid(), null);
    }

    /**
     * Verifica si un jugador tiene un trabajo específico.
     *
     * @param player Jugador a verificar.
     * @param trabajo Trabajo a comprobar.
     * @return Verdadero si el jugador tiene el trabajo, falso en caso contrario.
     */
    public static boolean tieneTrabajo(ServerPlayerEntity player, String trabajo) {
        return trabajo.equals(trabajos.get(player.getUuid()));
    }
}
