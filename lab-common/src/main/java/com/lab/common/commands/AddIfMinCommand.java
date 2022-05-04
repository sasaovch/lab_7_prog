package com.lab.common.commands;


import com.lab.common.data.User;
import com.lab.common.util.AskerInformation;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.BodyCommandWithSpMar;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;


public class AddIfMinCommand extends Command {
    private CollectionManager collectionManager;

    public AddIfMinCommand(CollectionManager collection) {
        super("add_if_min", "add_if_min {element} : add element if its value is less than minimal value in collection (value is health)");
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        BodyCommandWithSpMar bodyCommandWithSpMar = (BodyCommandWithSpMar) bodyCommand;
        bodyCommandWithSpMar.getSpaceMarine().setOwnerName(user.getLogin());
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
