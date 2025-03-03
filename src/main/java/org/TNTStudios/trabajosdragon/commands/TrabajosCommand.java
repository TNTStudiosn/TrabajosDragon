package org.TNTStudios.trabajosdragon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.TNTStudios.dragoneconomy.EconomyManager;

import java.util.concurrent.CompletableFuture;

public class TrabajosCommand {
    /**
     * Registra el comando "/DragonEconomy dar <jugador> <cantidad>".
     */
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("DragonEconomy")
                    .requires(source -> hasPermission(source, "DragonEconomy.admin")) // Solo usuarios con permisos
                    .then(CommandManager.literal("dar")
                            .then(CommandManager.argument("jugador", StringArgumentType.string())
                                    .suggests(TrabajosCommand::suggestPlayers) // Añadir autocompletado
                                    .then(CommandManager.argument("cantidad", IntegerArgumentType.integer(1))
                                            .executes(TrabajosCommand::executeDarCommand) // Separar la lógica en un método separado
                                    )
                            )
                    )
            );
        });
    }

    /**
     * Método ejecutado cuando se ejecuta el comando "/DragonEconomy dar".
     *
     * @param context Contexto del comando.
     * @return Código de éxito del comando.
     */
    private static int executeDarCommand(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!hasPermission(source, "DragonEconomy.admin")) {
            source.sendMessage(
                    Text.literal("⚠ No tienes permisos para ejecutar este comando.").formatted(Formatting.RED));
            return 0;
        }

        String playerName = StringArgumentType.getString(context, "jugador");
        int amount = IntegerArgumentType.getInteger(context, "cantidad");

        ServerPlayerEntity sender = source.getPlayer();
        ServerPlayerEntity receiver = source.getServer().getPlayerManager().getPlayer(playerName);

        if (sender != null && receiver != null) {
            // Prevenir transferencias negativas o exageradamente grandes
            if (amount <= 0) {
                source.sendMessage(
                        Text.literal("⚠ La cantidad debe ser un número positivo.").formatted(Formatting.RED));
                return 1;
            }

            if (amount > 1000000) { // Límite arbitrario para prevenir abusos
                source.sendMessage(
                        Text.literal("⚠ No puedes transferir una cantidad tan grande.").formatted(Formatting.RED));
                return 1;
            }

            // Añadir dinero al jugador receptor
            EconomyManager.addMoney(receiver.getUuid(), amount);
            // Sincronizar el balance con el cliente receptor
            EconomyManager.sendBalanceToClient(receiver);

            // Enviar mensaje de confirmación solo al emisor
            sender.sendMessage(
                    Text.literal("✔ Se han enviado $" + amount + " a " + playerName).formatted(Formatting.GREEN));

            // Enviar mensaje de confirmación solo al receptor
            receiver.sendMessage(
                    Text.literal("✔ Has recibido $" + amount + " de un administrador.").formatted(Formatting.GREEN), false);
        } else {
            source.sendMessage(
                    Text.literal("⚠ Jugador no encontrado.").formatted(Formatting.RED));
        }
        return 1;
    }

    /**
     * Verifica si el ejecutante del comando tiene el permiso "DragonEconomy.admin".
     *
     * @param source Fuente del comando.
     * @return {@code true} si tiene el permiso, {@code false} en caso contrario.
     */
    private static boolean hasPermission(ServerCommandSource source, String permission) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUuid());
            if (user != null) {
                return user.getCachedData()
                        .getPermissionData(luckPerms.getContextManager().getQueryOptions(user)
                                .orElse(QueryOptions.defaultContextualOptions())) // Manejo del Optional
                        .checkPermission(permission)
                        .asBoolean();
            }
        }
        return false;
    }


    /**
     * Proveedor de sugerencias que lista los nombres de los jugadores en línea.
     *
     * @param context Contexto del comando.
     * @param builder Constructor de sugerencias.
     * @return Un CompletableFuture con las sugerencias.
     */
    private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        ServerCommandSource source = context.getSource();
        String input = builder.getRemaining().toLowerCase();
        source.getServer().getPlayerManager().getPlayerList().stream()
                .map(ServerPlayerEntity::getName)
                .map(Text::getString)
                .filter(name -> name.toLowerCase().startsWith(input))
                .sorted()
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
