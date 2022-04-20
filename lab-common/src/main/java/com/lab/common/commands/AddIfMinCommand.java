package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;


public class AddIfMinCommand extends Command {

    @Override
    public CommandResult run(Object data, SpaceMarine spMar, CollectionManager collection) {
        if (collection.addIfMin(spMar)) {
            return new CommandResult("add_if_min", spMar.getName() + " has been successfully added.", true);
        } else {
            return new CommandResult("add_if_min", "Element is bigger than minimal.", false);
        }
    }
}
