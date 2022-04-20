package com.lab.common.commands;

import java.util.ArrayList;
import java.util.Collections;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;

public class PrintDescendingCommand extends Command {

    @Override
    public CommandResult run(Object data, SpaceMarine spMar, CollectionManager collection) {
        if (collection.getSize() == 0) {
            return new CommandResult("print_descending", "The collection is empty.", true);
        }
        ArrayList<SpaceMarine> list = collection.sortCollection();
        Collections.reverse(list);
        return new CommandResult("print_descending", list, true);
    }
}
