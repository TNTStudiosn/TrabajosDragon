package org.TNTStudios.trabajosdragon;

import net.fabricmc.api.ModInitializer;
import org.TNTStudios.trabajosdragon.commands.TrabajoCommand;
import org.TNTStudios.trabajosdragon.commands.TrabajosCommand;
import org.TNTStudios.trabajosdragon.trabajos.EventoCazador;
import org.TNTStudios.trabajosdragon.trabajos.EventoLenador;
import org.TNTStudios.trabajosdragon.trabajos.EventoMinero;

public class Trabajosdragon implements ModInitializer {

    @Override
    public void onInitialize() {
        TrabajosCommand.register();
        TrabajoCommand.registrar();
        EventoMinero.registrar();
        EventoLenador.registrar();
        EventoCazador.registrar();
        DataManager.initialize();
    }
}
