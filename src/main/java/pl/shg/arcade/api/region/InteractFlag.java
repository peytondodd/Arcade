/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.region;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import pl.shg.arcade.api.Material;
import pl.shg.arcade.api.human.Player;
import pl.shg.arcade.api.location.Block;
import pl.shg.arcade.api.team.Team;

/**
 *
 * @author Aleksander
 */
public class InteractFlag extends Flag {
    private List<Block> allowed;
    private Team owner;
    
    public InteractFlag() {
        super("interact");
        this.setAllowed(new ArrayList<Block>());
        this.setOwner(null);
    }
    
    public InteractFlag(List<Block> allowed) {
        super("interact");
        this.setAllowed(allowed);
        this.setOwner(null);
    }
    
    public InteractFlag(Team owner) {
        super("interact");
        this.setAllowed(new ArrayList<Block>());
        this.setOwner(owner);
    }
    
    public InteractFlag(List<Block> allowed, Team owner) {
        super("interact");
        this.setAllowed(allowed);
        this.setOwner(owner);
    }
    
    public InteractFlag(List<Block> allowed, Team owner, String message) {
        super("interact", message);
        this.setAllowed(allowed);
        this.setOwner(owner);
    }
    
    @Override
    public boolean canInteract(Player player, Material item) {
        if (!player.isTeam(this.getOwner())) {
            if (this.getAllowed().isEmpty()) {
                return false;
            }
            for (Block v : this.getAllowed()) {
                if (v.getMaterial().asString().equals(item.asString())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public List<Block> getAllowed() {
        return this.allowed;
    }
    
    public Team getOwner() {
        return this.owner;
    }
    
    private void setAllowed(List<Block> allowed) {
        Validate.notNull(allowed, "allowed can not be null");
        this.allowed = allowed;
    }
    
    private void setOwner(Team owner) {
        this.owner = owner;
    }
}
