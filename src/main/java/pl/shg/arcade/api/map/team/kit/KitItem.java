/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.map.team.kit;

import pl.shg.arcade.api.Material;
import pl.shg.arcade.api.inventory.Item;
import pl.shg.arcade.api.util.Validate;

/**
 *
 * @author Aleksander
 */
public class KitItem extends Item {
    private final String id;
    private int slot = -1;
    
    public KitItem(String id, Material type, int amount) {
        super(type, amount);
        Validate.notNull(id, "id can not be null");
        this.id = id;
    }
    
    public String getID() {
        return this.id;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public boolean hasSlot() {
        return this.slot >= 0;
    }
    
    public void setSlot(int slot) {
        this.slot = slot;
    }
}
