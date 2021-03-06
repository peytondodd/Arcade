/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit.module.destroyable;

import pl.shg.arcade.api.event.CancelableEvent;
import pl.shg.arcade.api.human.Player;

/**
 *
 * @author Aleksander
 */
public class MonumentTouchEvent extends CancelableEvent {
    private Monument monument;
    private Player player;
    
    public MonumentTouchEvent(Monument monument, Player player) {
        super(MonumentTouchEvent.class);
        this.setMonument(monument);
        this.setPlayer(player);
    }
    
    public Monument getMonument() {
        return this.monument;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    private void setMonument(Monument monument) {
        this.monument = monument;
    }
    
    private void setPlayer(Player player) {
        this.player = player;
    }
}
