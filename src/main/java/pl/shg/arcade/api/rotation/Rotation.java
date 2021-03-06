/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.rotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.Validate;
import pl.shg.arcade.api.map.Map;

/**
 *
 * @author Aleksander
 */
public class Rotation {
    private final List<Map> maps;
    
    public Rotation() {
        this.maps = new ArrayList<>();
    }
    
    public void addMap(Map map) {
        Validate.notNull(map, "map can not be null");
        this.maps.add(map);
    }
    
    public Collection<Map> getMaps() {
        return this.maps;
    }
}
