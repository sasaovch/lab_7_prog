package com.lab.common.commands;

import com.lab.common.util.AskerInformation;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.BodyCommandWithSpMar;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;


public class AddCommand extends Command {
    private CollectionManager collectionManager;

    public AddCommand() {
    }

    public AddCommand(CollectionManager collection) {
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, String userName) {
        BodyCommandWithSpMar bodyCommandWithSpMar = (BodyCommandWithSpMar) bodyCommand;
        bodyCommandWithSpMar.getSpaceMarine().setOwnerName(userName);
        if (collectionManager.addElement(bodyCommandWithSpMar.getSpaceMarine())) {
        return new CommandResult("add", null, true, bodyCommandWithSpMar.getSpaceMarine().getName() + " has been added.");
        } else {
            return new CommandResult("add", null, false, 
                bodyCommandWithSpMar.getSpaceMarine().getName() + " already exists" + " or database broke down");
        }
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) {
        if (args.length != 0) {
            return null;
        }
        return new BodyCommandWithSpMar(null, AskerInformation.askMarine(ioManager));
    }
}
