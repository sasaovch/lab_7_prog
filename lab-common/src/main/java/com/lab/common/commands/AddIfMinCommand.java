package com.lab.common.commands;


import com.lab.common.data.User;
import com.lab.common.util.AskerInformation;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.BodyCommandWithSpMar;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;


public class AddIfMinCommand extends Command {
    private CollectionManager collectionManager;

    public AddIfMinCommand() {
    }

    public AddIfMinCommand(CollectionManager collection) {
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User client) {
        BodyCommandWithSpMar bodyCommandWithSpMar = (BodyCommandWithSpMar) bodyCommand;
        bodyCommandWithSpMar.getSpaceMarine().setOwnerName(client.getLogin());
        switch (collectionManager.addIfMin(bodyCommandWithSpMar.getSpaceMarine())) {
            case True : return new CommandResult("add_if_min", null, true, bodyCommandWithSpMar.getSpaceMarine().getName() + " has been added.");
            case False :  return new CommandResult("add_if_min", null, false, "Element is bigger than minimum.");
            default :  return new CommandResult("add_if_min", null, false, "Database broke down.");
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
