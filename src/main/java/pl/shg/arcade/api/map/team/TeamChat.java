/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.map.team;

import java.util.logging.Level;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.Color;
import pl.shg.arcade.api.Log;
import pl.shg.arcade.api.chat.ActionMessageType;
import pl.shg.arcade.api.chat.ChatMessage;
import pl.shg.arcade.api.command.Sender;
import pl.shg.arcade.api.human.Player;
import pl.shg.arcade.api.util.Validate;

/**
 *
 * @author Aleksander
 */
public class TeamChat extends ChatChannel {
    private final Team team;
    
    public TeamChat(Team team) {
        super(true);
        Validate.notNull(team, "team can not be null");
        this.team = team;
    }
    
    @Override
    public String getFormat(String[] args) {
        Validate.notNull(args, "args can not be null");
        return args[0] + Color.RESET + Color.GRAY + ": " + args[1];
    }
    
    @Override
    public void sendActionMessage(ActionMessageType type, String message) {
        Validate.notNull(type, "type can not be null");
        Validate.notNull(message, "message can not be null");
        
        Log.log(Level.INFO, "[Action] " + message);
        for (Player player : this.getTeam().getPlayers()) {
            player.sendActionMessage(type, message);
        }
    }
    
    @Override
    public void sendServerMessage(Sender sender, String message) {
        Validate.notNull(message, "message can not be null");
        Log.log(Level.INFO, "[Chat] " + message);
        if (sender == null) {
            sender = Arcade.getCommands().getConsoleSender();
        }
        
        ChatMessage chat = new ChatMessage();
        chat.setSender(sender);
        chat.setText(message);
        
        for (Player player : this.getTeam().getPlayers()) {
            player.sendChatMessage(sender, chat);
        }
    }
    
    @Override
    public boolean testSpy(Sender sender, Player reciver) {
        if (sender instanceof Player) {
            return !((Player) sender).isTeam(reciver.getTeam());
        } else {
            return false;
        }
    }
    
    public Team getTeam() {
        return this.team;
    }
}
