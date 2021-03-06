/*
 * Copyright (C) 2015 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.bukkit.cy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import pl.shg.arcade.api.Arcade;
import pl.shg.arcade.api.Log;
import pl.shg.arcade.api.Material;
import pl.shg.arcade.api.item.Enchantment;
import pl.shg.arcade.api.item.EnchantmentType;
import pl.shg.arcade.api.item.Enchantments;
import pl.shg.arcade.api.item.Item;
import pl.shg.arcade.api.kit.Kit;
import pl.shg.arcade.api.kit.KitData;
import pl.shg.arcade.api.kit.KitItem;
import pl.shg.arcade.api.kit.KitItemBuilder;
import pl.shg.arcade.api.kit.KitType;
import pl.shg.arcade.api.kit.LeatherColorData;
import pl.shg.arcade.api.kit.Option;
import pl.shg.arcade.api.kit.PotionOption;
import pl.shg.arcade.api.team.TeamColor;

/**
 *
 * @author Aleksander
 */
public class CyKitsLoader {
    private final FileConfiguration f;
    private final String section = "kits";
    private List<Kit> kits;
    
    public CyKitsLoader(FileConfiguration f) {
        this.f = f;
        this.loadKits();
    }
    
    public KitData getData(String path) {
        if (this.f.get(path) == null) {
            return null;
        }
        
        for (String data : this.f.getConfigurationSection(path).getKeys(false)) {
            String value = this.f.getString(path + "." + data);
            
            switch (data.toLowerCase()) {
                case "leather-color":
                    return new LeatherColorData(TeamColor.valueOf(value.toUpperCase()));
            }
        }
        return null;
    }
    
    public List<Enchantment> getEnchantments(String path) {
        List<Enchantment> enchantments = new ArrayList<>();
        for (String configEnch : this.f.getStringList(path)) {
            String[] split = configEnch.split("-");
            
            String name = split[0];
            int level = 1;
            if (split.length > 1) {
                try {
                    level = Integer.valueOf(split[1]);
                } catch (NumberFormatException ex) {
                    Log.noteAdmins("Nie mozna zaladowac enchantu " + name + " w " +
                            path + " - " + ex.getMessage(), Log.NoteLevel.WARNING);
                }
            }
            
            EnchantmentType type = Enchantments.getEnchantment(name);
            if (type != null) {
                Enchantment enchantment = new Enchantment(type);
                enchantment.setLevel(level);
                enchantments.add(enchantment);
            } else {
                Log.noteAdmins("Enchant \"" + name + "\" w konfiguracji mapy nie zostal rozpoznany.", Log.NoteLevel.WARNING);
            }
        }
        return enchantments;
    }
    
    public List<KitItem> getItems(String path) {
        List<KitItem> items = new ArrayList<>();
        for (String item : this.f.getConfigurationSection(path + ".items").getKeys(false)) {
            String type = this.f.getString(path + ".items." + item + ".id");
            
            KitItemBuilder builder = new KitItemBuilder(item, new Material(type));
            builder.amount(this.f.getInt(path + ".items." + item + ".amount", Item.DEFAULT_AMOUNT));
            builder.name(this.f.getString(path + ".items." + item + ".name"));
            builder.description(this.f.getStringList(path + ".items." + item + ".description"));
            builder.slot(this.f.getInt(path + ".items." + item + ".slot", -1));
            builder.enchantments(this.getEnchantments(path + ".items." + item + ".enchantments"));
            builder.unbreakable(this.f.getBoolean(path + ".items." + item + ".unbreakable", false));
            builder.data(this.getData(path + ".items." + item + ".data"));
            
            items.add(builder.build());
        }
        return items;
    }
    
    public List<Kit> getKits() {
        return this.kits;
    }
    
    public List<Option> getOptions(String path) {
        List<Option> options = new ArrayList<>();
        if (this.f.isConfigurationSection(path + ".options")) {
            for (String option : this.f.getConfigurationSection(path + ".options").getKeys(false)) {
                // TODO get option and load it
                switch (option.toLowerCase()) {
                    case "potions":
                        options.addAll(this.getOptionPotions(path + ".options." + option.toLowerCase()));
                        break;
                }
            }
        }
        return options;
    }
    
    private List<PotionOption> getOptionPotions(String path) {
        List<PotionOption> list = new ArrayList<>();
        for (String potion : this.f.getConfigurationSection(path + ".potion").getKeys(false)) {
            String id = this.f.getString(path + "." + potion + ".id");
            int level = this.f.getInt(path + "." + potion + ".level");
            int time = this.f.getInt(path + "." + potion + ".time");
            list.add(new PotionOption(id, level, time));
        }
        return list;
    }
    
    private void loadKit(String kit) {
        String path = this.section + "." + kit;
        
        Kit obj = new Kit(kit);
        for (KitItem item : this.getItems(path)) {
            obj.registerItem(item);
        }
        for (Option option : this.getOptions(path)) {
            obj.registerOption(option);
        }
        
        Log.log(Level.INFO, "Zarejestrowano zestaw " + obj.getID() +
                " (" + obj.getItems().size() + " itemow, " + obj.getOptions().size() + " opcji)");
        this.kits.add(obj);
        
    }
    
    private void loadKits() {
        this.kits = new ArrayList<>();
        for (String kit : this.f.getConfigurationSection(this.section).getKeys(false)) {
            this.loadKit(kit);
        }
    }
    
    public static HashMap<KitType, List<Kit>> getDefinedKits(FileConfiguration f, String path) {
        HashMap<KitType, List<Kit>> kits = new HashMap<>();
        
        path += ".kits";
        if (f.get(path) == null) {
            return null;
        }
        
        for (String kit : f.getConfigurationSection(path).getKeys(false)) {
            KitType type;
            try {
                type = KitType.valueOf(kit.toUpperCase());
            } catch (IllegalArgumentException ex) {
                type = KitType.ACTION;
            }
            
            List<Kit> list = new ArrayList<>();
            
            List<String> listString = f.getStringList(path + "." + kit);
            for (String kitString : listString) {
                Kit kitObj = Arcade.getTeams().getKit(kitString);
                if (kitObj != null) {
                    list.add(kitObj);
                }
            }
            
            kits.put(type, list);
        }
        return kits;
    }
}
