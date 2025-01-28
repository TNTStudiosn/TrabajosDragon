package org.TNTStudios.trabajosdragon;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.dragoneconomy.EconomyManager;

public class PagoManager {

    /**
     * Realiza el pago a un jugador de manera centralizada.
     *
     * @param player  El jugador que recibirá el pago.
     * @param amount  La cantidad de dinero a otorgar.
     */
    public static void pagarJugador(ServerPlayerEntity player, int amount) {
        if (player == null || amount <= 0) {
            return;
        }

        // Agregar dinero al jugador
        EconomyManager.addMoney(player.getUuid(), amount);
        // Sincronizar el balance con el cliente
        EconomyManager.sendBalanceToClient(player);

        // Enviar mensaje de confirmación al jugador
        player.sendMessage(
                Text.literal("✔ Has recibido $" + amount + " por completar tu trabajo.")
                        .formatted(Formatting.GREEN),
                false
        );
    }
}
