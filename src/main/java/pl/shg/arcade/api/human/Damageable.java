/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.human;

/**
 *
 * @author Aleksander
 */
public interface Damageable {
    void damage(double amount);
    
    double getHealth();
    
    void setHealth(double health);
}
