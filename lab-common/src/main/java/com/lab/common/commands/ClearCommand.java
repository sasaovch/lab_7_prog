package com.lab.common.commands;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;

public class ClearCommand extends Command {
    private CollectionManager collectionManager;

    public ClearCommand(CollectionManager collection) {
        super("clear", "clear : clear the collection");
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        switch (collectionManager.clearCollection(user.getLogin())) {
            case True : return new CommandResult("clear", null, true, "The collection is cleared.");
            default :  return new CommandResult("clear", null, false, "Database broke down.");
        }
    }
}
