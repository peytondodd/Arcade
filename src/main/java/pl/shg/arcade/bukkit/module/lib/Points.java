/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit.module.lib;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.configuration.ConfigurationException;
import pl.shg.arcade.api.map.Tutorial;
import pl.shg.arcade.api.module.Library;
import pl.shg.arcade.api.module.Score;
import pl.shg.arcade.api.team.Team;
import pl.shg.arcade.api.text.Color;
import pl.shg.arcade.api.util.Version;
import pl.shg.arcade.bukkit.ScoreboardManager;

/**
 *
 * @author Aleksander
 */
public class Points extends Library {
    private static Library library;
    private static int maxScore = -1;
    private static final HashMap<Team, Integer> points = new HashMap<>();
    
    public Points() {
        super(new Date(2015, 03, 28), "points", Version.valueOf("1.0"));
        this.getDocs().setDescription("Biblioteka ta oferuje system punktów dla " +
                "drużyn. Obecnie biblioteka jest wykorzystywana poprzez moduł " +
                "<code>death-match</code>. Biblioteka nie posiada żadnej konfiguracji, " +
                "a jest jedynie API wykorzystywanym przez jeden lub wiele wspólnych " +
                "modułów.");
        this.deploy(true);
    }
    
    @Override
    public void disable() {}
    
    @Override
    public void enable() {}
    
    @Override
    public void load(File file) throws ConfigurationException {
        for (Team team : Arcade.getTeams().getTeams()) {
            points.put(team, 0);
        }
    }
    
    @Override
    public void unload() {
        points.clear();
    }
    
    @Override
    public Score[] getMatchInfo(Team team) {
        return new Score[] {
            Score.byID(team, "points", new Score(new String(),
                    Color.GOLD, "Punkty" + Color.RED + ": ", Color.DARK_AQUA + Color.BOLD + Points.get(team)))
        };
    }
    
    @Override
    public Tutorial.Page getTutorial() {
        return new Tutorial.Page("System punktów",
                "Zadaniem Twojej druzyny jest zdobycie najwiekszej liczny punktów.\n\n" +
                "Wygrywa druzyna która jako pierwsza zdobedzie limit - " + getMaxScore() + " punktów.");
    }
    
    @Override
    public void makeScoreboard() {
        for (Team team : Points.teams()) {
            ScoreboardManager.Sidebar.getScore(team.getID(), team.getDisplayName(), Points.get(team));
        }
    }
    
    @Override
    public boolean objectiveScored(Team team) {
        if (getMaxScore() == -1) {
            return false;
        } else {
            return get(team) >= getMaxScore();
        }
    }
    
    @Override
    public SortedMap<Integer, Team> sortTeams() {
        SortedMap<Integer, Team> sorted = new TreeMap<>();
        for (Team team : Points.teams()) {
            sorted.put(Points.get(team), team);
        }
        return sorted;
    }
    
    public static void add(Team team, int p) {
        set(team, get(team) + 1);
    }
    
    public static void addMaxScore(int score) {
        if (score < 0) {
            setMaxScore(-1);
        } else {
            setMaxScore(getMaxScore() + score);
        }
    }
    
    public static void addOne(Team team) {
        add(team, 1);
    }
    
    public static void del(Team team, int p) {
        set(team, get(team) - p);
    }
    
    public static void delOne(Team team) {
        del(team, 1);
    }
    
    public static int get(Team team) {
        if (points.containsKey(team)) {
            return points.get(team);
        } else {
            return 0;
        }
    }
    
    public static Library getLibrary() {
        if (library == null) {
            library = new Points();
        }
        return library;
    }
    
    public static int getMaxScore() {
        return maxScore;
    }
    
    public static void set(Team team, int p) {
        points.put(team, p);
        Arcade.getServer().checkEndMatch();
    }
    
    public static void setMaxScore(int score) {
        maxScore = score;
    }
    
    public static Set<Team> teams() {
        return points.keySet();
    }
}
