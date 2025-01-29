package org.TNTStudios.trabajosdragon.entidades;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.TNTStudios.trabajosdragon.trabajos.LimitePagoDiarioPescador;

import java.util.HashMap;
import java.util.UUID;

/**
 * Clase que representa al Pescador, un tipo de comerciante.
 */
public class PescadorEntity extends ComercianteEntity {
    private static final int LIMITE_DIARIO = 300;
    private static final LimitePagoDiarioPescador limitePago = new LimitePagoDiarioPescador();

    public PescadorEntity(EntityType<? extends ComercianteEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected String getTrabajo() {
        return "Pescador";
    }

    @Override
    protected void manejarVenta(ServerPlayerEntity player) {
        int totalPago = 0;

        // Definir las opciones de venta: Item, Cantidad por grupo, Pago por grupo
        // En este caso, Cantidad por grupo es 1
        SaleOption[] opcionesVenta = {
                new SaleOption(Items.COD, 1, 2),
                new SaleOption(Items.SALMON, 1, 2),
                new SaleOption(Items.BOW, 1, 10),
                new SaleOption(Items.BOOK, 1, 10)
        };

        for (SaleOption opcion : opcionesVenta) {
            while (player.getInventory().count(opcion.item) >= opcion.cantidad) {
                if (limitePago.getPagosDiarios().getOrDefault(player.getUuid(), 0) + opcion.pago > LIMITE_DIARIO) {
                    player.sendMessage(Text.literal("⚠ Has alcanzado el límite de dinero diario para este trabajo.")
                            .formatted(Formatting.RED), false);
                    return; // Salir de la venta si el límite se ha alcanzado
                }

                // Remover items y sumar el pago
                int pago = removerItems(player, opcion.item, opcion.cantidad, opcion.pago);
                if (pago > 0) {
                    totalPago += pago;
                    limitePago.agregarPago(player, pago);
                    pagarJugador(player, pago);
                } else {
                    // No se pudo remover suficiente cantidad, posiblemente debido a un error
                    player.sendMessage(Text.literal("⚠ No se pudo completar la venta de " + opcion.item.getName().getString())
                            .formatted(Formatting.RED), false);
                }
            }
        }

        if (totalPago == 0) {
            player.sendMessage(Text.literal("⚠ No tienes items suficientes para vender.")
                    .formatted(Formatting.RED), false);
        }
    }

    /**
     * Clase interna para definir opciones de venta.
     */
    private static class SaleOption {
        net.minecraft.item.Item item;
        int cantidad; // Cantidad de items por grupo
        int pago;     // Pago por grupo

        SaleOption(net.minecraft.item.Item item, int cantidad, int pago) {
            this.item = item;
            this.cantidad = cantidad;
            this.pago = pago;
        }
    }

    public static HashMap<UUID, Integer> getPagosDiarios() {
        return limitePago.getPagosDiarios();
    }

    public static void setPagosDiarios(HashMap<UUID, Integer> pagosCargados) {
        limitePago.setPagosDiarios(pagosCargados);
    }
}
