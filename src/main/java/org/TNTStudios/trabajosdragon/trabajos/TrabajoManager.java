package org.TNTStudios.trabajosdragon.trabajos;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.HashMap;
import java.util.UUID;

public class TrabajoManager {
    private static HashMap<UUID, String> trabajos = new HashMap<>();

    /**
     * Asigna un trabajo a un jugador, solo si no tiene uno ya.
     */
    public static boolean asignarTrabajo(ServerPlayerEntity player, String trabajo) {
        UUID uuid = player.getUuid();

        // Si el jugador ya tiene un trabajo, no puede cambiarlo
        if (trabajos.containsKey(uuid)) {
            return false;
        }

        trabajos.put(uuid, trabajo);
        return true;
    }

    /**
     * Obtiene el trabajo actual de un jugador.
     */
    public static String obtenerTrabajo(ServerPlayerEntity player) {
        return trabajos.getOrDefault(player.getUuid(), null);
    }

    /**
     * Permite a un operador (OP) eliminar el trabajo de un jugador.
     */
    public static boolean removerTrabajo(ServerPlayerEntity player) {
        return trabajos.remove(player.getUuid()) != null;
    }

    /**
     * Verifica si un jugador tiene un trabajo específico.
     */
    public static boolean tieneTrabajo(ServerPlayerEntity player, String trabajo) {
        return trabajo.equals(trabajos.get(player.getUuid()));
    }

    /**
     * Obtiene el mapa de trabajos.
     *
     * @return Mapa de trabajos.
     */
    public static HashMap<UUID, String> getTrabajos() {
        return trabajos;
    }

    /**
     * Establece el mapa de trabajos (usado por DataManager).
     *
     * @param trabajosCargados Mapa de trabajos cargados.
     */
    public static void setTrabajos(HashMap<UUID, String> trabajosCargados) {
        trabajos.clear();
        trabajos.putAll(trabajosCargados);
    }
}
