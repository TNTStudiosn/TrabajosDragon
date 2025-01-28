package org.TNTStudios.trabajosdragon.trabajos;

import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

public class LimitePagoDiarioLenador {
    private static final HashMap<UUID, Integer> pagosDiarios = new HashMap<>();
    private static LocalDate ultimaFecha = obtenerFechaCDMX();
    private static final int LIMITE_LENADOR = 300;

    public static boolean agregarPago(ServerPlayerEntity player, int cantidad) {
        resetearSiEsNecesario();

        UUID uuid = player.getUuid();
        int pagoActual = pagosDiarios.getOrDefault(uuid, 0);

        if (pagoActual >= LIMITE_LENADOR) {
            return false;
        }

        int nuevoSaldo = pagoActual + cantidad;
        if (nuevoSaldo > LIMITE_LENADOR) {
            cantidad = LIMITE_LENADOR - pagoActual;
        }

        pagosDiarios.put(uuid, nuevoSaldo);
        return true;
    }

    private static LocalDate obtenerFechaCDMX() {
        return LocalDate.now(ZoneId.of("America/Mexico_City"));
    }

    private static void resetearSiEsNecesario() {
        LocalDate fechaActual = obtenerFechaCDMX();
        if (!fechaActual.equals(ultimaFecha)) {
            pagosDiarios.clear();
            ultimaFecha = fechaActual;
        }
    }
}
