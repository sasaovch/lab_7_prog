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

    public void addCommand(String key, Command com) {
        commandsMap.put(key, com);
    }

    public Map<String, Command> getMap() {
        return commandsMap;
    }

    public Command getCommand(String name) {
        return commandsMap.get(name);
    }

    public static CommandManager getDefaultCommandManager(CollectionManager collMan, UserManagerInt userColl) {
        CommandManager cm = new CommandManager();
        cm.addCommand("help", new HelpCommand());
        cm.addCommand("info", new InfoCommand(collMan));
        cm.addCommand("show", new ShowCommand(collMan));
        cm.addCommand("clear", new ClearCommand(collMan));
        cm.addCommand("exit", new ExitCommand());
        cm.addCommand("group_counting_by_name", new GroupCountingByNameCommand(collMan));
        cm.addCommand("print_descending", new PrintDescendingCommand(collMan));
        cm.addCommand("add", new AddCommand(collMan));
        cm.addCommand("add_if_min", new AddIfMinCommand(collMan));
        cm.addCommand("remove_greater", new RemoveGreaterCommand(collMan));
        cm.addCommand("remove_lower", new RemoveLowerCommand(collMan));
        cm.addCommand("update", new UpdateCommand(collMan));
        cm.addCommand("remove_by_id", new RemoveByIdCommand(collMan));
        cm.addCommand("count_by_loyal", new CountByLoyalCommand(collMan));
        cm.addCommand("log in", new LogInCommand(userColl));
        cm.addCommand("sign up", new SignUpCommand(userColl));
        cm.addCommand("execute_script", new ExecuteScriptCommand());
        return cm;
    }
}
