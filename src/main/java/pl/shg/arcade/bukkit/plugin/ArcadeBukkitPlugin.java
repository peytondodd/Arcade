/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.bukkit.plugin;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.shg.arcade.ArcadeFactory;
import pl.shg.arcade.PluginImpl;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.Log;
import pl.shg.arcade.api.PluginProperties;
import pl.shg.arcade.api.command.def.VariableCommand;
import pl.shg.arcade.api.development.TestCommand;
import pl.shg.arcade.api.event.ArcadeEventListeners;
import pl.shg.arcade.api.event.Event;
import pl.shg.arcade.api.loader.DynamicMapLoader;
import pl.shg.arcade.api.loader.FileMapLoader;
import pl.shg.arcade.api.loader.Loader;
import pl.shg.arcade.api.loader.URLMapLoader;
import pl.shg.arcade.api.map.Map;
import pl.shg.arcade.api.module.Module;
import pl.shg.arcade.api.server.MiniGameServer;
import pl.shg.arcade.api.server.Role;
import pl.shg.arcade.bukkit.BukkitPermissionsManager;
import pl.shg.arcade.bukkit.BukkitPlayer;
import pl.shg.arcade.bukkit.BukkitPlayerManagement;
import pl.shg.arcade.bukkit.BukkitServer;
import pl.shg.arcade.bukkit.BukkitWorldManager;
import pl.shg.arcade.bukkit.cy.CyConfiguration;
import pl.shg.arcade.bukkit.listeners.*;
import pl.shg.arcade.bukkit.test.DragonDeathTest;
import pl.shg.arcade.bukkit.test.HologramTest;
import pl.shg.arcade.bukkit.test.SoundTest;
import pl.shg.arcade.bukkit.test.XPTest;
import pl.shg.commons.bukkit.BukkitCommons;
import pl.shg.commons.server.ArcadeTarget;
import pl.shg.commons.server.Servers;
import pl.shg.commons.server.TargetServer;
import pl.shg.shootgame.arcade.PrimitiveAntiLogout;
import pl.shg.shootgame.plugin.ServersLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksander
 */
public final class ArcadeBukkitPlugin extends JavaPlugin {
    public static final String PLUGIN_NAME = "Arcade";
    public static final String RUN_CMD = "-Xms1024M -Xmx1024M -jar sportbukkit-1.8-R0.1.jar";
    private static pl.shg.arcade.api.Plugin implementation;
    private static BukkitServer server;
    
    @Override
    public void onEnable() {
        this.loadServers();

        PluginProperties properties = this.loadBasics();
        this.registerOnlinePlayers();
        this.loadBukkitListeners();
        this.loadMaps(properties, 2);
        
        Log.log(Level.INFO, "Wczytywanie modulow...");
        new ModuleLoader() {
            @Override
            public void register(Class<? extends Module> module) {
                Validate.notNull(module, "module can not be null");
                try {
                    Arcade.getModules().register(module);
                } catch (Throwable ex) {
                    String id = module.getCanonicalName();
                    Log.noteAdmins("Napotkano blad w ladowaniu modulu " + id + " - patrz konsole", Log.NoteLevel.SEVERE);
                    Logger.getLogger(ModuleLoader.class.getName()).log(Level.SEVERE, "Napotkano blad podczas ladowania " + id, ex);
                }
            }
        }.init();
        
        for (Class<? extends Module> module : Arcade.getModules().getModules()) {
            Module object = Arcade.getModules().asObject(module);
            object.loadDependencies();
        }
        
        VariableCommand.setPerformer(new PerformerImpl());
        Arcade.getMaps().setWorlds(new BukkitWorldManager(this.getServer()));
    }
    
    @Override
    public void onDisable() {
        this.getServer().getPluginManager().callEvent(new ArcadeShutdownEvent((PluginImpl) getAPI(), this, getBukkit()));
    }
    
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new EmptyWorldGenerator();
    }
    
    private void checkDependency(String name) {
        if (this.getServer().getPluginManager().getPlugin(name) != null) {
            this.getLogger().log(Level.INFO, "Polaczono z pluginem {0}.", name);
        } else {
            this.getLogger().log(Level.SEVERE, "Nie znaleziono wymaganego pluginu: {0} - wylaczanie...", name);
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    private PluginProperties loadBasics() {
        // Checkout all dependencies first
        for (String dependency : this.getDescription().getDepend()) {
            this.checkDependency(dependency);
        }
        
        // Create Bukkit implementation of the Arcade API server
        server = new BukkitServer(RUN_CMD, Bukkit.getServer());
        
        // Make a new small setup properties to the API
        PluginProperties properties = new PluginProperties();
        properties.setCommands(new BukkitCommandExecutor());
        properties.setConfiguration(new CyConfiguration());
        properties.setMapsDirectory(this.getServer().getWorldContainer().getPath());
        properties.setPermissions(new BukkitPermissionsManager());
        properties.setPlayerManagement(new BukkitPlayerManagement(this.getServer()));
//        properties.setProxyServer(new BungeeCordProxy());
        properties.setSettingsDirectory(this.getDataFolder().getAbsolutePath());
        
        // Setup Arcade API
        implementation = ArcadeFactory.newInstance(getBukkit(), properties);
        Arcade.setPlugin(getAPI());
        
        // Call the server role enable method
        Role role = Arcade.getOptions().getRole();
        Log.log(Level.INFO, "Serwer jest uruchamiany jako \"" + role.getName() + "\".");
        role.getRole().onServerEnable();
        
        if (role.equals(Role.DEVELOPMENT)) { // it's ugly :(
            new DatabaseListeners().init(this);
        }
        
        BukkitCommandExecutor.createHelpTopic();
        
        this.registerTests();
        return properties;
    }
    
    @Deprecated
    private void loadBukkitCommands(String[] commands) {
        BukkitCommandExecutor executor = new BukkitCommandExecutor();
        for (String cmd : commands) {
            PluginCommand pluginCmd = this.getServer().getPluginCommand(cmd.toLowerCase());
            if (pluginCmd != null) {
                pluginCmd.setExecutor(executor);
            } else {
                Log.log(Level.WARNING, "Nieudana proba rejestracji komendy " + cmd.toLowerCase() + " - komenda nie istnieje.");
            }
        }
    }
    
    private void loadBukkitListeners() {
        PluginManager manager = this.getServer().getPluginManager();
        if (Arcade.getOptions().isBungeeCordEnabled()) {
            BungeeCordProxy.register(this.getServer());
            manager.registerEvents(new PingDataListeners(), this);
        } else {
            manager.registerEvents(new CustomMOTDListener(), this);
        }
        
        manager.registerEvents(new BukkitMenuListener(), this);
        manager.registerEvents(new CommonsListeners(), this);
        manager.registerEvents(new GameableBlockListeners(), this);
        manager.registerEvents(new InventorySpyListeners(), this);
        manager.registerEvents(new ObserverKitListeners(), this);
        manager.registerEvents(new ObserverListeners(), this);
        manager.registerEvents(new PlayerListeners(getBukkit()), this);
        manager.registerEvents(new PlayerMoveListener(), this);
        manager.registerEvents(new RegionListeners(), this);
        manager.registerEvents(new WorldListeners(), this);
        
        Event.registerListener(
                new ArcadeEventListeners(),
                new ModuleListeners()
        );
    }
    
    /**
     * Generating a list of maps and save. This will also invoke a
     * {@link #loadRotations()} method.
     * @param properties PluginProperites to get plugin's configuration
     * @param impl The type of implementation to use. The following values are
     * acceptable:<br />
     * <strong>0</strong> - load a list of specifited maps from the URL (must be
     * maps.txt file).<br />
     * <strong>1</strong> - load a list of specifited maps from flat (must be
     * maps.txt file).<br />
     * <strong>2</strong> - use dynamic loader, also method that loop are maps
     * in the directory and check it's validate of configuration.
     */
    private void loadMaps(PluginProperties properties, int impl) {
        Loader loader = null;
        switch (impl) {
            case 0:
                // getting a list of maps from GitHub
                loader = new URLMapLoader("https://raw.githubusercontent.com/ShootGame/Maps/master/maps.txt");
                break;
            case 1:
                // getting a list of maps from the maps directory
                loader = new FileMapLoader(new File(properties.getMapsDirectory() + File.separator + "maps.txt"));
                break;
            case 2:
                // getting a list of maps from the directory of maps
                loader = new DynamicMapLoader();
                break;
        }
        
        if (loader == null) {
            return;
        }
        
        Log.log(Level.INFO, "Ladowanie konfiguracji map...");
        loader.loadMapList();
        Arcade.getMaps().setMaps(loader.getMaps());
        
        StringBuilder builder = new StringBuilder();
        for (Map map : Arcade.getMaps().getMaps()) {
            builder.append(map.getDisplayName()).append(", ");
        }
        Log.log(Level.INFO, "Zaladowano mapy: " + builder.toString());
        
        this.loadRotations();
    }
    
    private void loadRotations() {
        Charset encoding = Charset.forName("UTF-8");
        
        if (Servers.getConfiguration() == null) {
            Log.log(Level.WARNING, "Nie znaleziono konfiguracji serwera.");
            return;
        }
        
        MiniGameServer.Online online = MiniGameServer.ONLINE;
        if (online.getShoot() == null) {
            Log.log(Level.WARNING, "Serwer online nie istnieje!");
            return;
        }
        
        String rotationLocation = Servers.getConfiguration().getString("arcade." + online.getShoot().getID() + ".rotation");
        if (rotationLocation == null) {
            Log.log(Level.WARNING, "Rotacja dla " + online.getShoot().getID() + " nie zostala znaleziona.");
            return;
        }
        
        MiniGameServer.loadRotation(rotationLocation, online.getRotation(), encoding);
        
        for (TargetServer arcade : Servers.getServers()) {
            if (arcade instanceof ArcadeTarget) {
                MiniGameServer miniGame = MiniGameServer.of((ArcadeTarget) arcade);
                String location = miniGame.getCommons().getSetting("rotation");
                MiniGameServer.loadRotation(location, miniGame.getRotation(), encoding);
            }
        }
    }

    private void loadServers() {
        this.saveDefaultConfig();

        String translations = this.getConfig().getString("translations", "translations");
        String configuration = this.getConfig().getString("server-list");
        String serverId = this.getConfig().getString("server-id", "Arcade");

        BukkitCommons.initialize(this, new File(translations));
        FileConfiguration target = null;

        try (InputStream input = new URL(configuration).openStream()){
            target = YamlConfiguration.loadConfiguration(input);
        } catch (IOException io) {
            io.printStackTrace();
        }

        if (target != null) {
            new ServersLoader(serverId, target).initialize();
        }

        this.getServer().getPluginManager().registerEvents(new PrimitiveAntiLogout(this), this);
        this.getServer().getPluginManager().registerEvents(new pl.shg.shootgame.listeners.PlayerListeners(), this);
    }
    
    private void registerOnlinePlayers() {
        int reg = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            server.addPlayer(new BukkitPlayer(player));
            reg++;
        }
        
        if (reg > 0) {
            Log.log(Level.INFO, "Zarejestrowano " + reg + " graczy online.");
        } else {
            Log.log(Level.INFO, "Nie zarejestrowano zadnych graczy online, poniewaz ich brak.");
        }
    }
    
    private void registerTests() {
        TestCommand.registerDefaults();
        new DragonDeathTest().register();
        new HologramTest().register();
        new SoundTest().register();
        new XPTest().register();
    }
    
    public static pl.shg.arcade.api.Plugin getAPI() {
        return ArcadeBukkitPlugin.implementation;
    }
    
    public static Plugin getPlugin() {
        return ArcadeBukkitPlugin.getPlugin(ArcadeBukkitPlugin.class);
    }
    
    public static BukkitServer getBukkit() {
        return server;
    }
}
