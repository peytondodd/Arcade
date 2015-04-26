/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api;

import pl.shg.arcade.api.human.Player;
import pl.shg.arcade.api.map.team.kit.KitType;

/**
 *
 * @author Aleksander
 */
public interface PlayerManagement {
    void playSound(Player player, Sound sound);
    
    void refreshHiderForAll();
    
    void setAsObserver(Player player, boolean fullKit, boolean hider);
    
    void setAsPlayer(Player player, KitType kit, boolean hider, boolean sendTitle);
}
