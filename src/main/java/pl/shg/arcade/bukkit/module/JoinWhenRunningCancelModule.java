/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit.module;

import java.io.File;
import java.util.Date;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.event.Event;
import pl.shg.arcade.api.event.EventListener;
import pl.shg.arcade.api.event.PlayerJoinTeamEvent;
import pl.shg.arcade.api.map.ConfigurationException;
import pl.shg.arcade.api.map.team.ObserverTeamBuilder;
import pl.shg.arcade.api.match.MatchStatus;
import pl.shg.arcade.api.module.Module;
import pl.shg.arcade.api.module.docs.ConfigurationDoc;
import pl.shg.arcade.bukkit.Config;

/**
 *
 * @author Aleksander
 */
public class JoinWhenRunningCancelModule extends Module {
    private EventListener listener;
    
    public JoinWhenRunningCancelModule() {
        super(new Date(2015, 4, 19), "join-when-running-cancel", "1.0");
        this.getDocs().setDescription("Ten moduł wyłącza możliwość dołączania " +
                "do drużyn (także rangom premium) w czasie trawnia meczu. Do " +
                "gry można dołączyć tylko i wyłącznie podczas odliczania do " +
                "jej startu.");
        this.addExample(new ConfigurationDoc() {
            @Override
            public String getPrefix() {
                return "Moduł posiada opcję zmiany wiadomości próby wejścia do " +
                        "drużyny. Jest to <code>error message</code>, a więc " +
                        "kolor czerwony zostanie dodany automatycznie.";
            }
            
            @Override
            public String[] getCode() {
                return new String[] {
                    "join-when-running-cancel:",
                    "  message: 'Nie mozesz dolaczyc do druzyny w czasie trawania meczu na tej mapie.'"
                };
            }
        });
        this.deploy(true);
    }
    
    @Override
    public void disable() {}
    
    @Override
    public void enable() {}
    
    @Override
    public void load(File file) throws ConfigurationException {
        String message = Config.getValueMessage(Config.get(file), this,
                "Nie mozesz dolaczyc do druzyny w czasie trawania meczu na tej mapie.", false);
        this.listener = new PlayerJoinTeam(message);
        Event.registerListener(this.listener);
    }
    
    @Override
    public void unload() {
        Event.unregisterListener(this.listener);
    }
    
    private class PlayerJoinTeam implements EventListener {
        private final String message;
        
        public PlayerJoinTeam(String message) {
            this.message = message;
        }
        
        @Override
        public Class<? extends Event> getEvent() {
            return PlayerJoinTeamEvent.class;
        }
        
        @Override
        public void handle(Event event) {
            PlayerJoinTeamEvent e = (PlayerJoinTeamEvent) event;
            if (Arcade.getMatches().getStatus() == MatchStatus.PLAYING &&
                    !e.getTeam().getID().equals(ObserverTeamBuilder.getTeamID())) {
                e.setCancel(true);
                e.getPlayer().sendError(this.message);
            }
        }
    }
}
