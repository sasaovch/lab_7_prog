package com.lab.common.commands;

import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;

public class InfoCommand extends Command {
    private CollectionManager collectionManager;

    public InfoCommand() {
    }

    public InfoCommand(CollectionManager collection) {
        collectionManager = collection;
    }


    @Override
    public CommandResult run(BodyCommand bodyCommand, String userName) {
        TreeMap<String, Object> outMap = new TreeMap<>();
        outMap.put("Initialization time", collectionManager.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        outMap.put("Number of Marines", collectionManager.getSize());
        outMap.put("Type", SpaceMarine.class);
        StringBuilder messageResult = new StringBuilder();
        outMap.entrySet().stream().forEach(s -> messageResult.append(s.getKey() + ": " + s.getValue() + "\n"));
        return new CommandResult("info", outMap, true, messageResult.toString());
    }
}
