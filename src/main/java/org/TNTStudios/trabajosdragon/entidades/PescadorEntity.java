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

        totalPago += removerItems(player, Items.COD, 1, 2);
        totalPago += removerItems(player, Items.SALMON, 1, 2);
        totalPago += removerItems(player, Items.BOW, 1, 10);
        totalPago += removerItems(player, Items.BOOK, 1, 10);

        if (totalPago > 0 && limitePago.agregarPago(player, totalPago)) {
            pagarJugador(player, totalPago);
        } else {
            player.sendMessage(Text.literal("⚠ Has alcanzado el límite de dinero diario para este trabajo.")
                    .formatted(Formatting.RED), false);
        }
    }

    public static HashMap<UUID, Integer> getPagosDiarios() {
        return limitePago.getPagosDiarios();
    }

    public static void setPagosDiarios(HashMap<UUID, Integer> pagosCargados) {
        limitePago.setPagosDiarios(pagosCargados);
    }
}
