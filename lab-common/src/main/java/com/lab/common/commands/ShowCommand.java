package com.lab.common.commands;

import java.io.Serializable;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;

public class ShowCommand extends Command {
    private CollectionManager collectionManager;

    public ShowCommand(CollectionManager collection) {
        super("show", "show : print all elements of collection");
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        if (collectionManager.getSize() == 0) {
            return new CommandResult("show", null, true, "The collection is empty.");
        }
        StringBuilder messagResult = new StringBuilder();
        collectionManager.sortByCoordinates().stream().forEach(spMar -> messagResult.append(spMar.toString() + "\n"));
        return new CommandResult("show", (Serializable) collectionManager.sortByCoordinates(), true, messagResult.toString());
    }
}
