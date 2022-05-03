package com.lab.common.commands;

import java.util.ArrayList;
import java.util.Collections;

import com.lab.common.data.SpaceMarine;
import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;

public class PrintDescendingCommand extends Command {
    private CollectionManager collectionManager;

    public PrintDescendingCommand() {
    }

    public PrintDescendingCommand(CollectionManager collection) {
        collectionManager = collection;
    }


    @Override
    public CommandResult run(BodyCommand bodyCommand, User client) {
        if (collectionManager.getSize() == 0) {
            return new CommandResult("print_descending", null, true, "The collection is empty.");
        }
        ArrayList<SpaceMarine> list = collectionManager.sortCollection();
        Collections.reverse(list);
        StringBuilder messageResult = new StringBuilder();
        list.stream().forEach(spMar -> messageResult.append(spMar.toString() + "\n"));
        return new CommandResult("print_descending", list, true, messageResult.toString());
    }
}
