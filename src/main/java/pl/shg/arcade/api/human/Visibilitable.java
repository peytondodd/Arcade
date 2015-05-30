/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.api.human;

/**
 *
 * @author Aleksander
 */
public interface Visibilitable {
    boolean canSee(Player player);
    
    void updateVisibility();
}
