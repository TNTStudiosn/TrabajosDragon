package org.TNTStudios.trabajosdragon.client.render;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import org.TNTStudios.trabajosdragon.entidades.*;

public class ComercianteRenderer extends LivingEntityRenderer<ComercianteEntity, PlayerEntityModel<ComercianteEntity>> {

    private static final Identifier TEXTURA_AGRICULTOR = new Identifier("trabajosdragon", "textures/entity/agricultor.png");
    private static final Identifier TEXTURA_PESCADOR = new Identifier("trabajosdragon", "textures/entity/pescador.png");
    private static final Identifier TEXTURA_CARTOGRAFO = new Identifier("trabajosdragon", "textures/entity/cartografo.png");
    private static final Identifier TEXTURA_CARNICERO = new Identifier("trabajosdragon", "textures/entity/carnicero.png");

    public ComercianteRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(net.minecraft.client.render.entity.model.EntityModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public Identifier getTexture(ComercianteEntity entity) {
        if (entity instanceof AgricultorEntity) {
            return TEXTURA_AGRICULTOR;
        } else if (entity instanceof PescadorEntity) {
            return TEXTURA_PESCADOR;
        } else if (entity instanceof CartografoEntity) {
            return TEXTURA_CARTOGRAFO;
        } else if (entity instanceof CarniceroEntity) {
            return TEXTURA_CARNICERO;
        }
        return TEXTURA_AGRICULTOR; // default
    }
}
