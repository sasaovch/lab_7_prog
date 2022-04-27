package com.lab.common.commands;

import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;

public class ClearCommand extends Command {
    private CollectionManager collectionManager;

    public ClearCommand() {
    }

    public ClearCommand(CollectionManager collection) {
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, Long userID) {
        if (collectionManager.clearCollection()) {
            return new CommandResult("clear", null, true,"The collection is cleared.");
        }
        return new CommandResult("clear", null, false, "Something went wrong. Try again.");
    }
}
