package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;

public class ClearCommand extends Command {


    @Override
    public CommandResult run(Object data, SpaceMarine spMar, CollectionManager collection) {
        collection.clearCollection();
        return new CommandResult("clear", "The collection is cleared.", true);
    }
}
