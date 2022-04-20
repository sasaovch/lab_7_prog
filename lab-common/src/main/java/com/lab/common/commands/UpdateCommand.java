package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;

public class UpdateCommand extends Command {

    @Override
    public CommandResult run(Object data, SpaceMarine spMar, CollectionManager collection) {
        Long id = (Long) data;
        if (collection.getSize() == 0) {
            return new CommandResult("update", "There are no such element in the collection.", false);
        }
        SpaceMarine changeMarine = collection.findByID(id);
        if (changeMarine == null) {
            return new CommandResult("update", "Id is not correct.", false);
        }
        collection.updateSpaceMarine(changeMarine, spMar);
        return new CommandResult("update", "Marine has been successfully updated.", true);
    }
}
