package org.TNTStudios.trabajosdragon.entidades;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.TNTStudios.dragoneconomy.EconomyManager;

import java.util.Collections;

public abstract class ComercianteEntity extends LivingEntity {

    protected ComercianteEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            manejarVenta(serverPlayer);
            return ActionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    protected abstract void manejarVenta(ServerPlayerEntity player);

    protected void pagarJugador(ServerPlayerEntity player, int cantidad) {
        EconomyManager.addMoney(player.getUuid(), cantidad);
        EconomyManager.sendBalanceToClient(player);
        player.sendMessage(Text.literal("✔ Has recibido $" + cantidad + " por tu venta.").formatted(Formatting.GREEN), false);
    }

    @Override
    public net.minecraft.util.Arm getMainArm() {
        return net.minecraft.util.Arm.RIGHT;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        // No hacer nada, ya que estas entidades no necesitan equipamiento
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
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

    protected int removerItems(ServerPlayerEntity player, Item item, int cantidad, int pago) {
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
