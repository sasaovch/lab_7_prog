package com.lab.common.commands;

import java.io.Serializable;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;

public class ShowCommand extends Command {

    @Override
    public CommandResult run(Object data, SpaceMarine spMar, CollectionManager collection) {
        if (collection.getSize() == 0) {
            return new CommandResult("show", "The collection is empty.", true);
        }
        return new CommandResult("show", (Serializable) collection.sortByCoordinates(), true);
    }
}
