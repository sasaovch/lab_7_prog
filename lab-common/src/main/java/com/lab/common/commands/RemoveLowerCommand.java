package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;

public class RemoveLowerCommand extends Command {

    @Override
    public CommandResult run(Object data, SpaceMarine spMar, CollectionManager collection) {
        if (collection.removeIf(spaceMar -> {
                return (spaceMar.compareTo(spMar) < 0);
            })) {
            return new CommandResult("remove_lower", "All items have been successfully deleted.", true);
        } else {
            return new CommandResult("remove_lower", "No element has been deleted.", true);
        }
    }
}
