/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit.module.boss;

import java.io.File;
import java.util.Date;
import java.util.SortedMap;
import pl.shg.arcade.api.configuration.ConfigurationException;
import pl.shg.arcade.api.map.Tutorial;
import pl.shg.arcade.api.module.ObjectiveModule;
import pl.shg.arcade.api.module.Score;
import pl.shg.arcade.api.team.Team;
import pl.shg.arcade.api.util.Version;

/**
 *
 * @author Aleksander
 */
public class BossModule extends ObjectiveModule {
    public BossModule() {
        super(new Date(2015, 5, 16), "boss", Version.valueOf("1.0"));
    }
    
    @Override
    public void disable() {
        
    }
    
    @Override
    public void enable() {
        
    }
    
    @Override
    public void load(File file) throws ConfigurationException {
        
    }
    
    @Override
    public void unload() {
        
    }
    
    @Override
    public Score[] getMatchInfo(Team team) {
        return null;
    }
    
    @Override
    public Tutorial.Page getTutorial() {
        return new Tutorial.Page("Kill the boss",
                "Zadaniem Twojej druzyny jest zabicie bossa druzyny przeciwnej.\n\n" +
                "Wygrywa druzyna która jako pierwsza zlikwiduje bossa przeciwnika.");
    }
    
    @Override
    public void makeScoreboard() {
        
    }
    
    @Override
    public boolean objectiveScored(Team team) {
        return false;
    }
    
    @Override
    public SortedMap<Integer, Team> sortTeams() {
        return null;
    }
}
