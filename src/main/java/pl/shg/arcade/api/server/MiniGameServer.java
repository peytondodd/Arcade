/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2015
 */
package pl.shg.arcade.api.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.Validate;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.map.Map;
import pl.shg.arcade.api.map.NotLoadedMap;
import pl.shg.arcade.api.rotation.Rotation;
import pl.shg.arcade.api.util.TextFileReader;
import pl.shg.commons.server.ArcadeTarget;
import pl.shg.commons.server.OnlineServer;
import pl.shg.commons.server.Servers;

/**
 *
 * @author Aleksander
 */
public class MiniGameServer {
    public static final Online ONLINE = new Online();
    private static final HashMap<ArcadeTarget, MiniGameServer> servers = new HashMap<>();
    
    private final ArcadeTarget commons;
    private final Rotation rotation = new Rotation();
    
    public MiniGameServer(ArcadeTarget commons) {
        Validate.notNull(commons, "commons can not be null");
        this.commons = commons;
    }
    
    public ArcadeTarget getCommons() {
        return this.commons;
    }
    
    public Rotation getRotation() {
        return this.rotation;
    }
    
    public static void loadRotation(String url, Rotation rotation, Charset encoding) {
        try {
            Validate.notNull(rotation, "rotation can not be null");
            
            List<String> lines = new ArrayList<>();
            InputStream input = new URL(url).openStream();
            InputStreamReader inputReader = new InputStreamReader(input, encoding);
            BufferedReader buffReader = new BufferedReader(inputReader);
            
            String str = null;
            while ((str = buffReader.readLine()) != null) {
                lines.add(str);
            }
            
            TextFileReader reader = new TextFileReader(lines);
            for (TextFileReader.Line line : reader.getLines()) {
                if (!line.isSetting()) {
                    Map map = Arcade.getMaps().getMapExact(line.getValue());
                    if (map != null) {
                        rotation.addMap(map);
                    } else {
                        rotation.addMap(new NotLoadedMap(line.getValue()));
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MiniGameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static MiniGameServer of(ArcadeTarget server) {
        Validate.notNull(server, "server can not be null");
        if (!servers.containsKey(server)) {
            servers.put(server, new MiniGameServer(server));
        }
        return servers.get(server);
    }
    
    public static class Online {
        private final Rotation rotation = new Rotation();
        
        public Rotation getRotation() {
            return this.rotation;
        }
        
        public OnlineServer getShoot() {
            return Servers.getOnline();
        }
    }
}
