package com.lab.common.commands;

import com.lab.common.exception.CommandArgumentException;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;

public class RemoveByIdCommand extends Command {
    private CollectionManager collectionManager;

    public RemoveByIdCommand() {
    }

    public RemoveByIdCommand(CollectionManager collection) {
        collectionManager = collection;
    }


    @Override
    public CommandResult run(BodyCommand bodyCommand, Long userID) {
        if (collectionManager.removeIf(spMar -> spMar.getID().equals(bodyCommand.getData()))) {
            return new CommandResult("remove_by_id", null, true, "SpaceMarine has been removed");
        } else {
            return new CommandResult("remove_by_id", null, false, "Uknown Id.");
        }
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) throws CommandArgumentException {
        if (args.length != 1) {
            throw new CommandArgumentException();
        }
        try {
            return new BodyCommand(Long.parseLong(args[0]));
        } catch (NumberFormatException e) {
            throw new CommandArgumentException();
        }
    }
}
