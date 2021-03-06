/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.command.def;

import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.command.Command;
import pl.shg.arcade.api.command.CommandException;
import pl.shg.arcade.api.command.Sender;
import pl.shg.arcade.api.map.Map;
import pl.shg.arcade.api.module.Module;
import pl.shg.arcade.api.module.ObjectiveModule;
import pl.shg.arcade.api.module.ScoreboardScore;
import pl.shg.arcade.api.team.Team;
import pl.shg.arcade.api.text.Color;

/**
 *
 * @author Aleksander
 */
public class MatchinfoCommand extends Command {
    public MatchinfoCommand() {
        super(new String[] {"matchinfo", "match", "game", "mecz", "gra"},
                "Pokazuje obecne informacje o meczu", "matchinfo");
        this.setPermission("arcade.command.matchinfo");
    }
    
    @Override
    public void execute(Sender sender, String[] args) throws CommandException {
        Map map = Arcade.getMaps().getCurrentMap();
        sender.sendMessage(Command.getTitle("Mecz", Color.GRAY + "(" + map.getDisplayName() + ")"));
        
        String prefix = "        ";
        for (Team team : Arcade.getTeams().getTeams()) {
            sender.sendMessage(Color.GRAY + "Cele dla " + team.getDisplayName() + Color.RESET + Color.GRAY + ":");
            boolean dataGiven = false;
            
            for (Module module : Arcade.getModules().getActiveModules()) {
                if (module instanceof ObjectiveModule) {
                    ScoreboardScore[] info = ((ObjectiveModule) module).getMatchInfo(team);
                    if (info != null) {
                        dataGiven = true;
                        for (ScoreboardScore score : info) {
                            if (score == null) {
                                continue;
                            }
                            
                            StringBuilder builder = new StringBuilder();
                            if (score.getPrefix() != null) {
                                builder.append(score.getPrefix());
                            }
                            if (score.getName() != null) {
                                builder.append(score.getName());
                            }
                            if (score.getSuffix() != null) {
                                builder.append(score.getSuffix());
                            }
                            sender.sendMessage(prefix + builder.toString());
                        }
                    }
                }
            }
            
            if (!dataGiven) {
                sender.sendError(prefix + Color.ITALIC + "Brak dostepnych celów dla tej druzyny.");
            }
        }
    }
    
    @Override
    public int minArguments() {
        return 0;
    }
}
