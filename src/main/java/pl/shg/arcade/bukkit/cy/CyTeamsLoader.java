/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.bukkit.cy;

import java.util.ArrayList;
import java.util.List;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.team.ObserverTeamBuilder;
import pl.shg.arcade.api.team.PlayableTeamBuilder;
import pl.shg.arcade.api.team.Team;
import pl.shg.arcade.api.team.TeamColor;
import org.bukkit.configuration.file.FileConfiguration;
import pl.shg.arcade.api.channels.GlobalChannel;
import pl.shg.arcade.api.channels.TeamsChannel;
import pl.shg.arcade.api.location.Spawn;

/**
 *
 * @author Aleksander
 */
public class CyTeamsLoader {
    private final FileConfiguration f;
    private boolean obs;
    private final String section = "teams";
    private List<Team> teams;
    
    public CyTeamsLoader(FileConfiguration f) {
        this.f = f;
        this.loadTeams();
    }
    
    public List<Team> getTeams() {
        return this.teams;
    }
    
    public boolean hasObs() {
        return this.obs;
    }
    
    private List<Spawn> getSpawns(String team) {
        List<Spawn> spawns = new ArrayList<>();
        for (String path : this.f.getConfigurationSection(this.section + "." + team + ".spawns").getKeys(false)) {
            path = this.section + "." + team + ".spawns." + path;
            double x = this.f.getDouble(path + ".x");
            int y = this.f.getInt(path + ".y");
            double z = this.f.getDouble(path + ".z");
            float yaw = 0;
            float pitch = 0;
            if (this.f.getInt(path + ".yaw") != 0 || this.f.getInt(path + ".pitch") != 0) {
                yaw = (float) this.f.getInt(path + ".yaw");
                pitch = (float) this.f.getInt(path + ".pitch");
            }
            spawns.add(new Spawn(x, y, z, yaw, pitch, null));
        }
        return spawns;
    }
    
    private void loadObs() {
        ObserverTeamBuilder builder = new ObserverTeamBuilder();
        builder.setSpawns(this.getSpawns(builder.getID()));
        
        Team team = new Team(builder);
        team.setChat(new GlobalChannel());
        for (Spawn spawn : builder.getSpawns()) {
            spawn.setTeam(team);
        }
        Arcade.getTeams().setObservers(team);
    }
    
    private void loadTeam(String team) {
        String path = this.section + "." + team;
        
        PlayableTeamBuilder builder = new PlayableTeamBuilder(team);
        builder.setFrendlyFire(this.f.getBoolean(path + ".frendly", true));
        builder.setMinimum(this.f.getInt(path + ".minimum", 1));
        builder.setName(team);
        builder.setSlots(this.f.getInt(path + ".slots"));
        builder.setSpawns(this.getSpawns(team));
        builder.setTeamColor(TeamColor.valueOf(this.f.getString(path + ".color", TeamColor.BLACK.toString()).toUpperCase()));
        
        Team t = new Team(builder);
        t.setChat(new TeamsChannel(t));
        t.setKits(CyKitsLoader.getDefinedKits(this.f, path));
        for (Spawn spawn : builder.getSpawns()) {
            spawn.setTeam(t);
        }
        this.teams.add(t);
    }
    
    private void loadTeams() {
        this.teams = new ArrayList<>();
        for (String path : this.f.getConfigurationSection(this.section).getKeys(false)) {
            if (path.equals(ObserverTeamBuilder.getTeamID()) && !this.obs) {
                this.obs = true;
                this.loadObs();
            } else {
                this.loadTeam(path);
            }
        }
    }
}
