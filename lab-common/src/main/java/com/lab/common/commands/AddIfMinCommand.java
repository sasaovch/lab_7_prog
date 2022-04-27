package com.lab.common.commands;


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
    public CommandResult run(BodyCommand bodyCommand, Long userID) {
        BodyCommandWithSpMar bodyCommandWithSpMar = (BodyCommandWithSpMar) bodyCommand;
        if (collectionManager.addIfMin(bodyCommandWithSpMar.getSpaceMarine())) {
            return new CommandResult("add_if_min", null, true, bodyCommandWithSpMar.getSpaceMarine().getName() + " has been added." );
        }
        return new CommandResult("add_if_min", null, false, "Element wasn't added.");
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) {
        if (args.length != 0) {
            return null;
        }
        return new BodyCommandWithSpMar(null, AskerInformation.askMarine(ioManager));
    }
}
