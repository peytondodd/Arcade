/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.bukkit.cy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.Log;
import pl.shg.arcade.api.configuration.Configuration;
import pl.shg.arcade.api.configuration.ConfigurationException;
import pl.shg.arcade.api.configuration.ConfigurationTechnology;
import pl.shg.arcade.api.map.Tutorial;
import pl.shg.arcade.api.module.Module;
import pl.shg.arcade.api.module.ModuleManager;
import pl.shg.arcade.api.module.ObjectiveModule;
import pl.shg.arcade.api.protocol.Protocol;
import pl.shg.arcade.api.util.Version;
import pl.shg.arcade.bukkit.ScoreboardManager;

/**
 *
 * @author Aleksander
 */
public class CyConfiguration implements ConfigurationTechnology {
    public static final String YAML_BUILD = "YAML-DO-NOT-TOUCH";
    private FileConfiguration config;
    private Configuration configuration;
    
    protected enum CError {
        NOT_SET, UNKNOWN_VALUE;
    }
    
    protected enum CLevel {
        RECOMMENDED;
    }
    
    @Override
    public void load(Configuration configuration, boolean test) throws ConfigurationException {
        this.config = YamlConfiguration.loadConfiguration(configuration.getFile());
        this.configuration = configuration;
        this.loadConfiguration(test);
    }
    
    @Override
    public String name() {
        return "cy";
    }
    
    @Override
    public void unload() {
        
    }
    
    @Override
    public void registerModules() throws ConfigurationException {
        ScoreboardManager.Sidebar.clear();
        
        ModuleManager modules = Arcade.getModules();
        for (String moduleString : this.getFile().getConfigurationSection("modules").getKeys(false)) {
            Class<? extends Module> clazz = modules.getModuleExact(moduleString);
            if (clazz == null) {
                throw new ConfigurationException("Modul " + moduleString + " nie istnieje");
            }
            
            Module module = modules.asObject(clazz);
            module.load(this.configuration.getFile());
            modules.active(module);
            
            if (module instanceof ObjectiveModule) {
                // Tutorial
                Tutorial.Page tutorial = ((ObjectiveModule) module).getTutorial();
                if (tutorial != null) {
                    Arcade.getMaps().getCurrentMap().getTutorial().addPage(tutorial);
                }
                
                // Scoreboard
                ((ObjectiveModule) module).makeScoreboard();
            }
        }
        
        ScoreboardManager.Sidebar.newScoreboard();
    }
    
    private Configuration getConfiguration() {
        return this.configuration;
    }
    
    private FileConfiguration getFile() {
        return this.config;
    }
    
    private void loadConfiguration(boolean test) throws ConfigurationException {
        String build = this.config.getString(CyConfiguration.YAML_BUILD);
        if (build == null) {
            this.throwError(CError.NOT_SET, YAML_BUILD, new Object[] {"cy"});
        } else if (build.startsWith("cy;proto=")) {
            Protocol proto = Protocol.byVersion(Version.valueOf(build.split("cy;proto=")[1].toUpperCase()));
            if (Protocol.getCurrent().toVersion().isOlderThan(proto.toVersion())) {
                Log.noteAdmins("Nie udalo sie zaladowac mapy " + this.getFile().getName() +
                        ", poniewaz jej protokol jest nowszy, niz obecna wersja Arcade.");
            } else {
                CyLoader loader = new CyLoader(this.getFile(), this.getConfiguration().getMap(), proto);
                loader.load(test);
            }
        } else {
            this.throwError(CError.UNKNOWN_VALUE, YAML_BUILD, new Object[] {"cy"});
        }
    }
    
    protected void throwError(CError error, String var, Object[] available) throws ConfigurationException {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(this.getConfiguration().getFile().getPath()).append(") Wartosc ").append(var);
        switch (error) {
            case NOT_SET:
                builder.append(" nie jest ustawiona!");
                break;
            case UNKNOWN_VALUE:
                builder.append(" nie jest poprawna!");
                break;
        }
        
        if (available != null) {
            builder.append("\nDostepne wartosci: ");
            for (Object o : available) {
                builder.append("'").append(String.valueOf(o)).append("', ");
            }
        }
        throw new ConfigurationException(builder.toString());
    }
    
    protected void throwWarning(CLevel level, String var) throws ConfigurationException {
        StringBuilder builder = new StringBuilder();
        builder.append("Wartosc ").append(var);
        switch (level) {
            case RECOMMENDED:
                builder.append(" jest rekomendowana!");
                break;
        }
        throw new ConfigurationException(builder.toString());
    }
}
