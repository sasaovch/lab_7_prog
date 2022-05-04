package com.lab.common.commands;

import java.util.List;
import java.util.TreeMap;

import com.lab.common.data.SpaceMarine;
import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;

public class GroupCountingByNameCommand extends Command {
    private CollectionManager collectionManager;

    public GroupCountingByNameCommand(CollectionManager collection) {
        super("group_counting_by_name", "group_counting_by_name : groups the elements of the collection by the value of the name field");
        collectionManager = collection;
    }


    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
            if (collectionManager.getSize() == 0) {
                return new CommandResult("group_counting_by_name", null, true, "The collection is empty.");
            }
            TreeMap<String, List<SpaceMarine>> outMap = new TreeMap<>(collectionManager.groupByField(SpaceMarine::getName));
            StringBuilder messageResult = new StringBuilder();
            outMap.entrySet().stream().forEach(s -> messageResult.append(s.getKey() + ": " + s.getValue().size() + "\n"));
            return new CommandResult("group_counting_by_name", outMap, true, messageResult.toString());
    }
}
