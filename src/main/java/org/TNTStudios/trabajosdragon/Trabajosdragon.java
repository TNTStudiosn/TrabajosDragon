package org.TNTStudios.trabajosdragon;

import net.fabricmc.api.ModInitializer;
import org.TNTStudios.trabajosdragon.commands.TrabajoCommand;
import org.TNTStudios.trabajosdragon.commands.TrabajosCommand;
import org.TNTStudios.trabajosdragon.trabajos.EventoMinero;

public class Trabajosdragon implements ModInitializer {

    @Override
    public void onInitialize() {
        TrabajosCommand.register();
        TrabajoCommand.registrar();
        EventoMinero.registrar();
    }
}
