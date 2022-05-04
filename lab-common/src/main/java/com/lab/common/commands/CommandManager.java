package com.lab.common.commands;

import java.util.HashMap;
import java.util.Map;

import com.lab.common.util.CollectionManager;
import com.lab.common.util.UserManagerInt;

public class CommandManager {
    private final Map<String, Command> commandsMap;

    public CommandManager() {
        commandsMap = new HashMap<String, Command>();
    }

    public void addCommand(Command com) {
        commandsMap.put(com.getName(), com);
    }

    public Map<String, Command> getMap() {
        return commandsMap;
    }

    public Command getCommand(String name) {
        return commandsMap.get(name);
    }

    public static CommandManager getDefaultCommandManager(CollectionManager collMan, UserManagerInt userColl) {
        CommandManager cm = new CommandManager();
        cm.addCommand(new HelpCommand(cm));
        cm.addCommand(new InfoCommand(collMan));
        cm.addCommand(new ShowCommand(collMan));
        cm.addCommand(new ClearCommand(collMan));
        cm.addCommand(new ExitCommand());
        cm.addCommand(new GroupCountingByNameCommand(collMan));
        cm.addCommand(new PrintDescendingCommand(collMan));
        cm.addCommand(new AddCommand(collMan));
        cm.addCommand(new AddIfMinCommand(collMan));
        cm.addCommand(new RemoveGreaterCommand(collMan));
        cm.addCommand(new RemoveLowerCommand(collMan));
        cm.addCommand(new UpdateCommand(collMan));
        cm.addCommand(new RemoveByIdCommand(collMan));
        cm.addCommand(new CountByLoyalCommand(collMan));
        cm.addCommand(new LogInCommand(userColl));
        cm.addCommand(new SignUpCommand(userColl));
        cm.addCommand(new ExecuteScriptCommand());
        return cm;
    }
}
