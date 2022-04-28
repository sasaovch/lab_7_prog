package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.AskerInformation;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.BodyCommandWithSpMar;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;

public class UpdateCommand extends Command {
    private CollectionManager collectionManager;

    public UpdateCommand() {
    }

    public UpdateCommand(CollectionManager collection) {
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, String userName) {
        BodyCommandWithSpMar bodyCommandWithSpMar = (BodyCommandWithSpMar) bodyCommand;
        SpaceMarine newSpaceMarine = bodyCommandWithSpMar.getSpaceMarine();
        newSpaceMarine.setOwnerName(userName);
        Long id = (Long) bodyCommand.getData();
        if (collectionManager.getSize() == 0) {
            return new CommandResult("update", null, false, "There are no such element in the collection.");
        }
        if (collectionManager.updateSpaceMarine(newSpaceMarine, id)) {
            return new CommandResult("update", null, true, "Marine has been successfully updated.");
        } else {
            return new CommandResult("update", null, false, "Id is not correct or insufficient access rights.");
        }
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) {
        if (args.length != 1) {
            return null;
        }
        try {
            return new BodyCommandWithSpMar(Long.parseLong(args[0]), AskerInformation.askMarine(ioManager));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
