// Ejemplo: AgricultorEntity.java
package org.TNTStudios.trabajosdragon.entidades;

import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.TNTStudios.trabajosdragon.trabajos.LimitePagoDiario;

public class AgricultorEntity extends ComercianteEntity {
    private static final int LIMITE_DIARIO = 400;

    public AgricultorEntity(EntityType<? extends ComercianteEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void manejarVenta(ServerPlayerEntity player) {
        int totalPago = 0;

        totalPago += removerItems(player, Items.WHEAT, 5, 1);
        totalPago += removerItems(player, Items.CARROT, 5, 1);
        totalPago += removerItems(player, Items.POTATO, 5, 1);
        totalPago += removerItems(player, Items.MELON, 5, 2);
        totalPago += removerItems(player, Items.PUMPKIN, 5, 2);
        totalPago += removerItems(player, Items.NETHER_WART, 5, 5);

        if (totalPago > 0 && LimitePagoDiario.agregarPago(player, totalPago)) {
            pagarJugador(player, totalPago);
        } else {
            player.sendMessage(Text.literal("⚠ Has alcanzado el límite de dinero diario para este trabajo.").formatted(Formatting.RED), false);
        }
    }
}
