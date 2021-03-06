/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit.module.wool;

import pl.shg.arcade.api.location.Block;
import pl.shg.arcade.api.team.Team;
import pl.shg.arcade.api.text.Color;
import pl.shg.arcade.api.util.SimpleFactory;

/**
 *
 * @author Aleksander
 */
public class WoolFactory extends SimpleFactory<Wool> {
    private final WoolModule module;
    
    public WoolFactory(WoolModule module) {
        this.module = module;
        this.register();
    }
    
    @Override
    public Wool build() {
        if (this.canBuild()) {
            Wool wool = new Wool(
                    (Color.Wool) this.get("color"),
                    (Team) this.get("owner"));
            WoolMonument monument = new WoolMonument(
                    (Block) this.get("block"),
                    this.getModule(),
                    wool);
            
            wool.setMonument(monument);
            this.getModule().registerMonument(monument);
        }
        
        return null;
    }
    
    public WoolModule getModule() {
        return this.module;
    }
    
    private void register() {
        this.register("block", false, Block.class);
        this.register("color", false, Color.Wool.class);
        this.register("owner", false, Team.class);
    }
}
