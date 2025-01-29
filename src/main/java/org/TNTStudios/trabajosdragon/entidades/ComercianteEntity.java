package org.TNTStudios.trabajosdragon.entidades;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.TNTStudios.trabajosdragon.PagoManager;
import org.TNTStudios.trabajosdragon.trabajos.TrabajoManager;

import java.util.Collections;

/**
 * Clase base para todas las entidades comerciantes.
 */
public abstract class ComercianteEntity extends LivingEntity {

    protected ComercianteEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            String trabajo = TrabajoManager.obtenerTrabajo(serverPlayer);
            if (trabajo == null) {
                serverPlayer.sendMessage(Text.literal("⚠ No tienes un trabajo asignado para interactuar con este comerciante.")
                        .formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }

            // Verificar si el trabajo del jugador coincide con la entidad
            if (!trabajo.equalsIgnoreCase(getTrabajo())) {
                serverPlayer.sendMessage(Text.literal("⚠ No puedes interactuar con este comerciante porque tu trabajo no coincide.")
                        .formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }

            manejarVenta(serverPlayer);
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    /**
     * Devuelve el nombre del trabajo asociado a esta entidad.
     */
    protected abstract String getTrabajo();

    /**
     * Maneja la lógica de venta y pago al jugador.
     */
    protected abstract void manejarVenta(ServerPlayerEntity player);

    /**
     * Realiza el pago al jugador.
     */
    protected void pagarJugador(ServerPlayerEntity player, int cantidad) {
        PagoManager.pagarJugador(player, cantidad);
    }

    @Override
    public net.minecraft.util.Arm getMainArm() {
        return net.minecraft.util.Arm.RIGHT;
    }

    @Override
    public void equipStack(net.minecraft.entity.EquipmentSlot slot, ItemStack stack) {
        // No hacer nada, ya que estas entidades no necesitan equipamiento
    }

    @Override
    public ItemStack getEquippedStack(net.minecraft.entity.EquipmentSlot slot) {
        return ItemStack.EMPTY; // No tiene equipamiento
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList(); // No tiene armaduras
    }

    /**
     * Devuelve los atributos base para esta entidad.
     */
    public static DefaultAttributeContainer.Builder createComercianteAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0) // Salud base
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0); // No se mueve
    }

    /**
     * Remueve una cantidad específica de un item del inventario del jugador y retorna el pago correspondiente.
     */
    protected int removerItems(ServerPlayerEntity player, net.minecraft.item.Item item, int cantidad, int pago) {
        int cantidadRemovida = 0;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                int cantidadAEliminar = Math.min(stack.getCount(), cantidad - cantidadRemovida);
                stack.decrement(cantidadAEliminar);
                cantidadRemovida += cantidadAEliminar;
                if (cantidadRemovida >= cantidad) break;
            }
        }
        return cantidadRemovida >= cantidad ? pago : 0;
    }
}
