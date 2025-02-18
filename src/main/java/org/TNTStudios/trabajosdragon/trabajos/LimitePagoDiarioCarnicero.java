package org.TNTStudios.trabajosdragon.trabajos;

import net.minecraft.server.network.ServerPlayerEntity;
import org.TNTStudios.trabajosdragon.DataManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

/**
 * Maneja los límites de pago diarios para el trabajo de Carnicero.
 */
public class LimitePagoDiarioCarnicero {
    private static HashMap<UUID, Integer> pagosDiarios = new HashMap<>();
    private static LocalDate ultimaFecha = obtenerFechaCDMX();
    private static final int LIMITE_CARNICERO = 400;

    /**
     * Agrega una cantidad de pago al jugador, respetando el límite diario.
     */
    public static boolean agregarPago(ServerPlayerEntity player, int cantidad) {
        resetearSiEsNecesario();

        UUID uuid = player.getUuid();
        int pagoActual = pagosDiarios.getOrDefault(uuid, 0);

        if (pagoActual >= LIMITE_CARNICERO) {
            return false; // Límite alcanzado.
        }

        int nuevoSaldo = Math.min(pagoActual + cantidad, LIMITE_CARNICERO);
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
     * Reinicia los límites de pago diario para todos los jugadores si ha pasado la medianoche.
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
