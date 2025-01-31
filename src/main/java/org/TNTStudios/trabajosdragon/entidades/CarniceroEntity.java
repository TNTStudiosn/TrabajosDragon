package org.TNTStudios.trabajosdragon.entidades;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.TNTStudios.trabajosdragon.trabajos.LimitePagoDiarioCarnicero;

import java.util.HashMap;
import java.util.UUID;

/**
 * Clase que representa al Carnicero, un tipo de comerciante.
 * Los jugadores con el trabajo "Carnicero" pueden venderle:
 * - 5 carnes (de cualquier tipo configurado abajo) por $2
 * - 5 cueros por $5
 */
public class CarniceroEntity extends ComercianteEntity {
    private static final int LIMITE_DIARIO = 400;  // Ajusta el límite si lo deseas
    private static final LimitePagoDiarioCarnicero limitePago = new LimitePagoDiarioCarnicero();

    public CarniceroEntity(EntityType<? extends ComercianteEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected String getTrabajo() {
        return "Carnicero";
    }

    @Override
    protected void manejarVenta(ServerPlayerEntity player) {
        int totalPago = 0;

        // Definimos nuestras "opciones de venta" para la carne y el cuero.
        // Por ejemplo, 5 de cualquiera de estas carnes = $2; 5 cueros = $5
        SaleOption[] opcionesVenta = {
                // Carnes crudas
                new SaleOption(Items.PORKCHOP, 5, 2),
                new SaleOption(Items.BEEF, 5, 2),
                new SaleOption(Items.CHICKEN, 5, 2),
                new SaleOption(Items.MUTTON, 5, 2),
                new SaleOption(Items.RABBIT, 5, 2),
                // (Opcional) Carnes cocinadas, si quieres que también las compre
                new SaleOption(Items.COOKED_PORKCHOP, 5, 3),
                new SaleOption(Items.COOKED_BEEF, 5, 3),
                new SaleOption(Items.COOKED_CHICKEN, 5, 3),
                new SaleOption(Items.COOKED_MUTTON, 5, 3),
                new SaleOption(Items.COOKED_RABBIT, 5, 3),
                // Cuero
                new SaleOption(Items.LEATHER, 5, 5),
        };

        // Iteramos por cada tipo de venta definida arriba
        for (SaleOption opcion : opcionesVenta) {
            // Mientras el jugador tenga al menos la cantidad mínima a vender...
            while (player.getInventory().count(opcion.item) >= opcion.cantidad) {
                // Revisamos si al sumar este pago alcanzamos o excedemos el límite diario
                int pagoAcumulado = limitePago.getPagosDiarios().getOrDefault(player.getUuid(), 0);
                if (pagoAcumulado + opcion.pago > LIMITE_DIARIO) {
                    player.sendMessage(Text.literal("⚠ Has alcanzado el límite de dinero diario para este trabajo.")
                            .formatted(Formatting.RED), false);
                    return;
                }

                // Removemos los ítems necesarios del inventario del jugador
                int pago = removerItems(player, opcion.item, opcion.cantidad, opcion.pago);
                if (pago > 0) {
                    totalPago += pago;
                    limitePago.agregarPago(player, pago);
                    pagarJugador(player, pago);
                } else {
                    // Si falla al remover la carne/cantidad por algún motivo, se lo notificamos
                    player.sendMessage(Text.literal("⚠ No se pudo completar la venta de "
                                    + opcion.item.getName().getString())
                            .formatted(Formatting.RED), false);
                    return;
                }
            }
        }

        // Si después de revisar todas las opciones el jugador no vendió nada, se lo indicamos
        if (totalPago == 0) {
            player.sendMessage(Text.literal("⚠ No tienes items suficientes para vender.")
                    .formatted(Formatting.RED), false);
        }
    }

    /**
     * Clase interna para manejar cada tipo de venta.
     * Puedes añadir/eliminar items, cambiar las cantidades o precios a tu gusto.
     */
    private static class SaleOption {
        net.minecraft.item.Item item;
        int cantidad; // Cuántos items hacen el "paquete"
        int pago;     // Cuánto se paga por ese "paquete"

        SaleOption(net.minecraft.item.Item item, int cantidad, int pago) {
            this.item = item;
            this.cantidad = cantidad;
            this.pago = pago;
        }
    }

    /**
     * Métodos para guardar/cargar la información de límite de pago diario.
     */
    public static HashMap<UUID, Integer> getPagosDiarios() {
        return limitePago.getPagosDiarios();
    }

    public static void setPagosDiarios(HashMap<UUID, Integer> pagosCargados) {
        limitePago.setPagosDiarios(pagosCargados);
    }
}
