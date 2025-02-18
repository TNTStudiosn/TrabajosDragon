package org.TNTStudios.trabajosdragon.trabajos;

import net.minecraft.server.network.ServerPlayerEntity;
import org.TNTStudios.trabajosdragon.DataManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

/**
 * Maneja los límites de pago diarios para el trabajo de Minero.
 */
public class LimitePagoDiario {
    private static HashMap<UUID, Integer> pagosDiarios = new HashMap<>();
    private static LocalDate ultimaFecha = obtenerFechaCDMX();
    private static final int LIMITE_MINERO = 500;

    /**
     * Agrega una cantidad de pago al jugador, respetando el límite diario.
     */
    public static boolean agregarPago(ServerPlayerEntity player, int cantidad) {
        resetearSiEsNecesario();

        UUID uuid = player.getUuid();
        int pagoActual = pagosDiarios.getOrDefault(uuid, 0);

        if (pagoActual >= LIMITE_MINERO) {
            return false; // Ya alcanzó el límite.
        }

        int nuevoSaldo = Math.min(pagoActual + cantidad, LIMITE_MINERO);
        pagosDiarios.put(uuid, nuevoSaldo);

        DataManager.saveData(); // Guardar cambios en el archivo
        return true;
    }

    /**
     * Obtiene la fecha actual en la zona horaria de CDMX.
     */
    private static LocalDate obtenerFechaCDMX() {
        return LocalDate.now(ZoneId.of("America/Mexico_City"));
    }

    /**
     * Reinicia los límites de pago diario si ha pasado la medianoche.
     */
    private static void resetearSiEsNecesario() {
        LocalDate fechaActual = obtenerFechaCDMX();
        if (!fechaActual.equals(ultimaFecha)) {
            resetearLimitesDiarios();
        }
    }

    /**
     * Reinicia los límites diarios de pago para todos los jugadores.
     */
    public static void resetearLimitesDiarios() {
        pagosDiarios.clear();
        ultimaFecha = obtenerFechaCDMX();
        DataManager.saveData();
    }

    /**
     * Obtiene el mapa de pagos diarios.
     */
    public static HashMap<UUID, Integer> getPagosDiarios() {
        resetearSiEsNecesario();
        return pagosDiarios;
    }

    /**
     * Establece el mapa de pagos diarios y lo guarda en el archivo.
     */
    public static void setPagosDiarios(HashMap<UUID, Integer> pagosCargados) {
        pagosDiarios = pagosCargados;
        DataManager.saveData();
    }
}
