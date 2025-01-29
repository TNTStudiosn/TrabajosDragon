package org.TNTStudios.trabajosdragon.entidades;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.TNTStudios.trabajosdragon.trabajos.LimitePagoDiario;

public class CartografoEntity extends ComercianteEntity {
    private static final int LIMITE_DIARIO = 400;

    public CartografoEntity(EntityType<? extends ComercianteEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void manejarVenta(ServerPlayerEntity player) {
        int totalPago = 0;

        totalPago += removerItems(player, Items.MAP, 1, 50);

        if (totalPago > 0 && LimitePagoDiario.agregarPago(player, totalPago)) {
            pagarJugador(player, totalPago);
        } else {
            player.sendMessage(Text.literal("⚠ Has alcanzado el límite de dinero diario para este trabajo.").formatted(Formatting.RED), false);
        }
    }
}
