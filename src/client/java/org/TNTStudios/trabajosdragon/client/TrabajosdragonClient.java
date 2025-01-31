package org.TNTStudios.trabajosdragon.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.TNTStudios.trabajosdragon.Trabajosdragon;
import org.TNTStudios.trabajosdragon.client.render.ComercianteRenderer;

public class TrabajosdragonClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registrar el renderer de las entidades
        EntityRendererRegistry.register(Trabajosdragon.AGRICULTOR, ComercianteRenderer::new);
        EntityRendererRegistry.register(Trabajosdragon.PESCADOR, ComercianteRenderer::new);
        EntityRendererRegistry.register(Trabajosdragon.CARTOGRAFO, ComercianteRenderer::new);
        EntityRendererRegistry.register(Trabajosdragon.CARNICERO, ComercianteRenderer::new);
    }
}
