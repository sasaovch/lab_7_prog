package com.lab.common.commands;

import java.util.Map.Entry;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;


public class HelpCommand extends Command {
    private CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help", "help : print info about all commands");
        this.commandManager = commandManager;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        StringBuilder result = new StringBuilder();
        for(Entry<String, Command> entryComm : commandManager.getMap().entrySet()) {
            result.append(entryComm.getValue().getDescription() + "\n");
        }
        return new CommandResult("help" , null, true, result.toString());
    }
}
