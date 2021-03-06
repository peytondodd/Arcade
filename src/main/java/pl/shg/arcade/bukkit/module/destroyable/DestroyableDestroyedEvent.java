/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit.module.destroyable;

import pl.shg.arcade.api.event.Event;
import pl.shg.arcade.api.human.Player;

/**
 *
 * @author Aleksander
 */
public class DestroyableDestroyedEvent extends Event {
    private Destroyable destroyable;
    private Player player;
    
    public DestroyableDestroyedEvent(Destroyable destroyable, Player player) {
        super(DestroyableDestroyedEvent.class);
        this.setDestroyable(destroyable);
        this.setPlayer(player);
    }
    
    public Destroyable getDestroyable() {
        return this.destroyable;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    private void setDestroyable(Destroyable destroyable) {
        this.destroyable = destroyable;
    }
    
    private void setPlayer(Player player) {
        this.player = player;
    }
}
