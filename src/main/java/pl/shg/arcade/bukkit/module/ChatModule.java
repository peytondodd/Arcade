/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.bukkit.module;

import java.io.File;
import java.util.Date;
import org.bukkit.configuration.file.FileConfiguration;
import pl.shg.arcade.api.Color;
import pl.shg.arcade.api.event.Event;
import pl.shg.arcade.api.event.EventListener;
import pl.shg.arcade.api.event.PlayerChatEvent;
import pl.shg.arcade.api.event.PlayerReceiveChatEvent;
import pl.shg.arcade.api.map.ConfigurationException;
import pl.shg.arcade.api.module.Module;
import pl.shg.arcade.api.module.docs.NotUsableDeprecation;
import pl.shg.arcade.bukkit.Config;

/**
 *
 * @author Aleksander
 */
public class ChatModule extends Module {
    private EventListener[] listeners;
    private String message;
    
    public ChatModule() {
        super(new Date(2015, 3, 27), "chat", "1.0");
        this.getDocs().setDeprecation(new NotUsableDeprecation(NotUsableDeprecation.Reason.AUTO_LOAD));
    }
    
    @Override
    public void disable() {}
    
    @Override
    public void enable() {}
    
    @Override
    public void load(File file) throws ConfigurationException {
        FileConfiguration config = Config.get(file);
        if (!Config.isSet(config, this)) {
            return;
        }
        this.listeners = new EventListener[] {
                new PlayerChat(this), new PlayerReceiveChat(this)
        };
        Event.registerListener(this.listeners);
        
        this.message = Color.translate("Nie udalo sie wyslac Twojej wiadomosci - %s!");
    }
    
    @Override
    public void unload() {
        Event.unregisterListener(this.listeners);
    }
    
    private class PlayerChat implements EventListener {
        private final ChatModule module;
        
        public PlayerChat(ChatModule module) {
            this.module = module;
        }
        
        @Override
        public Class<? extends Event> getEvent() {
            return PlayerChatEvent.class;
        }
        
        @Override
        public void handle(Event event) {
            PlayerChatEvent e = (PlayerChatEvent) event;
            if (!e.getSender().isConsole() && e.getMessage().isOffensive()) {
                e.setCancel(true);
                e.getSender().sendError(String.format(this.module.message, "Powstrzymaj slowa"));
                e.getSender().sendError(e.getMessage().getText());
            }
        }
    }
    
    private class PlayerReceiveChat implements EventListener {
        private final ChatModule module;
        
        public PlayerReceiveChat(ChatModule module) {
            this.module = module;
        }
        
        @Override
        public Class<? extends Event> getEvent() {
            return PlayerReceiveChatEvent.class;
        }
        
        @Override
        public void handle(Event event) {
            PlayerReceiveChatEvent e = (PlayerReceiveChatEvent) event;
            if (!e.getSender().isConsole() && e.getMessage().isOffensive()) {
                e.setCancel(true);
            }
        }
    }
}
