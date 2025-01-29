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

public class PescadorEntity extends ComercianteEntity {
    private static final int LIMITE_DIARIO = 300;

    public PescadorEntity(EntityType<? extends ComercianteEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void manejarVenta(ServerPlayerEntity player) {
        int totalPago = 0;

        totalPago += removerItems(player, Items.COD, 1, 2);
        totalPago += removerItems(player, Items.SALMON, 1, 2);
        totalPago += removerItems(player, Items.BOW, 1, 10);
        totalPago += removerItems(player, Items.BOOK, 1, 10);

        if (totalPago > 0 && LimitePagoDiario.agregarPago(player, totalPago)) {
            pagarJugador(player, totalPago);
        } else {
            player.sendMessage(Text.literal("⚠ Has alcanzado el límite de dinero diario para este trabajo.").formatted(Formatting.RED), false);
        }
    }
}
