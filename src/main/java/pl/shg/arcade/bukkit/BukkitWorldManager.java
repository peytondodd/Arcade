/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.bukkit;

import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.map.Map;
import pl.shg.arcade.api.map.WorldManager;
import pl.shg.arcade.api.util.Validate;
import pl.shg.arcade.bukkit.plugin.EmptyWorldGenerator;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 *
 * @author Aleksander
 */
public class BukkitWorldManager implements WorldManager {
    private final Server server;
    
    public BukkitWorldManager(Server server) {
        Validate.notNull(server, "server can not be null");
        this.server = server;
    }
    
    @Override
    public void load(String world) {
        Validate.notNull(world, "world can not be null");
        WorldCreator creator = new WorldCreator(world);
        creator.generator(new EmptyWorldGenerator());
        World w = this.server.createWorld(creator);
        w.setAutoSave(false);
    }
    
    @Override
    public void unloadCurrent() {
        this.unload(null);
    }
    
    @Override
    public void unload(String world) {
        Map map = Arcade.getMaps().getCurrentMap();
        if (world == null) {
            if (map != null) {
                world = map.getName();
            }
        }
        if (world != null) {
            this.server.unloadWorld(world, false);
        }
    }
}
