/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.Validate;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.Log;
import pl.shg.arcade.api.configuration.Configuration;
import pl.shg.arcade.api.configuration.ConfigurationTechnology;
import pl.shg.arcade.api.event.Event;
import pl.shg.arcade.api.map.Map;
import pl.shg.arcade.api.map.MapLoadEvent;
import pl.shg.arcade.api.map.MapManager;
import pl.shg.arcade.api.util.TextFileReader;

/**
 *
 * @author Aleksander
 */
public class FileMapLoader implements Loader {
    private final File file;
    private final List<Map> maps;
    
    public FileMapLoader(File file) {
        Validate.notNull(file, "file can not be null");
        this.file = file;
        this.maps = new ArrayList<>();
    }
    
    @Override
    public List<Map> getMaps() {
        return this.maps;
    }
    
    @Override
    public void loadMapList() {
        List<String> lines = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(this.file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replace(" ", "_");
                if (line.length() > Loader.MAX_NAME_LENGTH) {
                    continue;
                }
                
                File mapFile = new File(Arcade.getMaps().getMapsDirectory().getPath() + File.separator + line + File.separator + Configuration.FILE);
                if (mapFile.exists()) {
                    lines.add(line);
                } else {
                    Log.log(Level.WARNING, "Brak konfiguracji mapy " + line);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(URLMapLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        MapManager mapManager = Arcade.getMaps();
        for (TextFileReader.Line line : new TextFileReader(lines).getLines()) {
            Map map = new Map(null, line.getValue(), null, null);
            
            MapLoadEvent loadEvent = new MapLoadEvent(map);
            Event.callEvent(loadEvent);
            if (loadEvent.isCancel()) {
                return;
            }
            
            ConfigurationTechnology loader = mapManager.getConfiguration();
            loader.load(new Configuration(map), true);
            this.maps.add(map);
        }
    }
}
