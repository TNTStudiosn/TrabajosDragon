package org.TNTStudios.trabajosdragon.trabajos;

import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

/**
 * Maneja los límites de pago diarios para el trabajo de Carnicero.
 */
public class LimitePagoDiarioCarnicero {
    private static final HashMap<UUID, Integer> pagosDiarios = new HashMap<>();
    private static LocalDate ultimaFecha = obtenerFechaCDMX();
    // Ajusta este límite a tu gusto (400, 500, etc.)
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

        int nuevoSaldo = pagoActual + cantidad;
        if (nuevoSaldo > LIMITE_CARNICERO) {
            // Ajustamos para que no sobrepase
            cantidad = LIMITE_CARNICERO - pagoActual;
        }

        pagosDiarios.put(uuid, nuevoSaldo);
        return true;
    }

    /**
     * Devuelve la fecha actual en la zona horaria de CDMX.
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
            pagosDiarios.clear();
            ultimaFecha = fechaActual;
        }
    }

    public HashMap<UUID, Integer> getPagosDiarios() {
        return pagosDiarios;
    }

    public static void setPagosDiarios(HashMap<UUID, Integer> pagosCargados) {
        pagosDiarios.clear();
        pagosDiarios.putAll(pagosCargados);
    }
}
