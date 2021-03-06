/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.Validate;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import pl.shg.arcade.ArcadeServer;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.human.Player;
import pl.shg.arcade.api.menu.Menu;
import pl.shg.arcade.api.scheduler.SchedulerManager;
import pl.shg.arcade.api.text.Color;

/**
 *
 * @author Aleksander
 */
public class BukkitServer extends ArcadeServer {
    private final HashMap<UUID, Player> online;
    private final String runCmd;
    private final SchedulerManager scheduler;
    private final Server server;
    
    public BukkitServer(String runCmd, Server server) {
        Validate.notNull(runCmd, "runCmd can not be null");
        Validate.notNull(server, "server can not be null");
        this.runCmd = runCmd;
        this.online = new HashMap<>();
        this.scheduler = new BukkitSchedulerManager();
        this.server = server;
    }
    
    @Override
    public Object createMenu(Menu menu) {
        Inventory inventory = this.server.createInventory(null, menu.getSlots(), menu.getName());
        for (int slot : menu.getItems().keySet()) {
            inventory.setItem(slot, BukkitPlayerManagement.itemToStack(menu.getItem(slot)));
        }
        return inventory;
    }
    
    @Override
    public Collection<Player> getConnectedPlayers() {
        return online.values();
    }
    
    @Override
    public String getName() {
        return this.server.getServerName();
    }
    
    @Override
    public Player getPlayer(String name) {
        Validate.notNull(name, "name can not be null");
        Iterator<Player> it = this.getConnectedPlayers().iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.getName().toLowerCase().contains(name.toLowerCase())) {
                return player;
            }
        }
        return null;
    }
    
    @Override
    public Player getPlayer(UUID uuid) {
        Validate.notNull(uuid, "uuid can not be null");
        return this.online.get(uuid);
    }
    
    @Override
    public SchedulerManager getScheduler() {
        return scheduler;
    }
    
    @Override
    public int getSlots() {
        return this.server.getMaxPlayers();
    }
    
    @Override
    public void shutdown() {
        List<UUID> players = new ArrayList<>();
        for (Player player : this.getConnectedPlayers()) {
            players.add(player.getUUID());
        }
        
        for (UUID player : players) {
            Arcade.getServer().getPlayer(player).disconnect(Color.AQUA +
                    "Serwer jest teraz restartowany\n" + Color.YELLOW +
                    "Prosimy; " + Color.GOLD + "zaloguj sie ponownie.");
        }
        
        this.server.shutdown();
    }
    
    public void addPlayer(Player player) {
        Validate.notNull(player, "player can not be null");
        this.online.put(player.getUUID(), player);
    }
    
    public Server bukkit() {
        return this.server;
    }
    
    public String getRunCmd() {
        return this.runCmd;
    }
    
    public boolean removePlayer(Player player) {
        Validate.notNull(player, "player can not be null");
        Player remove = this.online.remove(player.getUUID());
        this.checkEndMatch();
        return remove != null;
    }
}
