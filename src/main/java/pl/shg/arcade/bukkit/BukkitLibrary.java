/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit;

import java.util.Date;
import pl.shg.arcade.api.module.Library;
import pl.shg.arcade.api.util.Version;

/**
 *
 * @author Aleksander
 */
@Deprecated
public abstract class BukkitLibrary extends Library implements BListener {
    public BukkitLibrary(Date date, String id, Version version) {
        super(date, id, version);
    }
    
    @Override
    public void disable() {
        Listeners.unregister(this);
    }
    
    @Override
    public void enable() {
        Listeners.register(this);
    }
}
