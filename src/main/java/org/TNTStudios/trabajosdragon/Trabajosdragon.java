package org.TNTStudios.trabajosdragon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.TNTStudios.trabajosdragon.commands.TrabajoCommand;
import org.TNTStudios.trabajosdragon.commands.TrabajosCommand;
import org.TNTStudios.trabajosdragon.entidades.AgricultorEntity;
import org.TNTStudios.trabajosdragon.entidades.CartografoEntity;
import org.TNTStudios.trabajosdragon.entidades.ComercianteEntity;
import org.TNTStudios.trabajosdragon.entidades.PescadorEntity;
import org.TNTStudios.trabajosdragon.trabajos.EventoCazador;
import org.TNTStudios.trabajosdragon.trabajos.EventoLenador;
import org.TNTStudios.trabajosdragon.trabajos.EventoMinero;

public class Trabajosdragon implements ModInitializer {

    public static final EntityType<AgricultorEntity> AGRICULTOR = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("trabajosdragon", "agricultor"),
            FabricEntityTypeBuilder.create().entityFactory(AgricultorEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.8f)).build()
    );

    public static final EntityType<PescadorEntity> PESCADOR = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("trabajosdragon", "pescador"),
            FabricEntityTypeBuilder.create().entityFactory(PescadorEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.8f)).build()
    );

    public static final EntityType<CartografoEntity> CARTOGRAFO = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("trabajosdragon", "cartografo"),
            FabricEntityTypeBuilder.create().entityFactory(CartografoEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.8f)).build()
    );


    @Override
    public void onInitialize() {
        TrabajosCommand.register();
        TrabajoCommand.registrar();
        EventoMinero.registrar();
        EventoLenador.registrar();
        EventoCazador.registrar();
        DataManager.initialize();

        // Registrar atributos para evitar NullPointerException en la carga de entidades
        FabricDefaultAttributeRegistry.register(AGRICULTOR, ComercianteEntity.createComercianteAttributes());
        FabricDefaultAttributeRegistry.register(PESCADOR, ComercianteEntity.createComercianteAttributes());
        FabricDefaultAttributeRegistry.register(CARTOGRAFO, ComercianteEntity.createComercianteAttributes());
    }

}
