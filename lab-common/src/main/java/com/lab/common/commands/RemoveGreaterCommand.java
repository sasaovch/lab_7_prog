package com.lab.common.commands;

import com.lab.common.util.AskerInformation;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.BodyCommandWithSpMar;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;

public class RemoveGreaterCommand extends Command {
    private CollectionManager collectionManager;

    public RemoveGreaterCommand() {
    }

    public RemoveGreaterCommand(CollectionManager collection) {
        collectionManager = collection;
    }


    @Override
    public CommandResult run(BodyCommand bodyCommand, String userName) {
        BodyCommandWithSpMar bodyCommWitSpMar = (BodyCommandWithSpMar) bodyCommand;
        if (collectionManager.removeIf(spaceMar -> {
            return (spaceMar.compareTo(bodyCommWitSpMar.getSpaceMarine()) > 0) && (spaceMar.getOwnerName().equals(userName));
            })) {
            return new CommandResult("remove_greater", null, true, "All items have been successfully deleted.");
        } else {
            return new CommandResult("remove_greater", null, true, "No element has been deleted.");
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
