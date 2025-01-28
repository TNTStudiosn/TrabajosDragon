package org.TNTStudios.trabajosdragon;

import net.fabricmc.api.ModInitializer;
import org.TNTStudios.trabajosdragon.commands.TrabajosCommand;

public class Trabajosdragon implements ModInitializer {

    @Override
    public void onInitialize() {
        TrabajosCommand.register();
    }
}
