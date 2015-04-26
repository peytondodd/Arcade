/*
 * Copyright (C) 2014 TheMolkaPL - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Aleksander Jagiełło <themolkapl@gmail.com>, 2014
 */
package pl.shg.arcade.api.command;

import java.util.List;

/**
 *
 * @author Aleksander
 */
public interface CommandManager {
    List<Command> getCommands();
    
    ConsoleSender getConsoleSender();
    
    void perform(String command, Sender sender, String[] args);
    
    void performAlias(String alias, Sender sender, String[] args);
    
    void registerCommand(Command command);
}
