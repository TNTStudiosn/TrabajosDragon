package org.TNTStudios.trabajosdragon.trabajos;

import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

public class LimitePagoDiarioCazador {
    private static final HashMap<UUID, Integer> pagosDiarios = new HashMap<>();
    private static LocalDate ultimaFecha = obtenerFechaCDMX();
    private static final int LIMITE_CAZADOR = 500;

    /**
     * Agrega una cantidad de pago al jugador, respetando el límite diario.
     *
     * @param player   Jugador que recibe el pago.
     * @param cantidad Cantidad a agregar.
     * @return {@code true} si se realizó el pago, {@code false} si el límite fue alcanzado.
     */
    public static boolean agregarPago(ServerPlayerEntity player, int cantidad) {
        resetearSiEsNecesario();

        UUID uuid = player.getUuid();
        int pagoActual = pagosDiarios.getOrDefault(uuid, 0);

        if (pagoActual >= LIMITE_CAZADOR) {
            return false;
        }

        int nuevoSaldo = pagoActual + cantidad;
        if (nuevoSaldo > LIMITE_CAZADOR) {
            cantidad = LIMITE_CAZADOR - pagoActual;
        }

        pagosDiarios.put(uuid, nuevoSaldo);
        return true;
    }

    /**
     * Obtiene la fecha actual en la zona horaria de CDMX.
     *
     * @return Fecha actual.
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

    /**
     * Obtiene el mapa de pagos diarios.
     *
     * @return Mapa de pagos diarios.
     */
    public static HashMap<UUID, Integer> getPagosDiarios() {
        return pagosDiarios;
    }

    /**
     * Establece el mapa de pagos diarios (usado por DataManager).
     *
     * @param pagosCargados Mapa de pagos diarios cargados.
     */
    public static void setPagosDiarios(HashMap<UUID, Integer> pagosCargados) {
        pagosDiarios.clear();
        pagosDiarios.putAll(pagosCargados);
    }
}
