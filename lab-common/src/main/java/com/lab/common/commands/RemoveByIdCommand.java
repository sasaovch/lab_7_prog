package com.lab.common.commands;

import com.lab.common.data.User;
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
    public CommandResult run(BodyCommand bodyCommand, User client) {
        switch (collectionManager.removeIf(spMar -> (spMar.getID().equals(bodyCommand.getData()) && (spMar.getOwnerName().equals(client.getLogin()))))) {
            case True : return new CommandResult("remove_by_id", null, true, "SpaceMarine has been removed.");
            case False :  return new CommandResult("remove_by_id", null, false, "Uknown Id or insufficient access rights.");
            default :  return new CommandResult("remove_by_id", null, false, "Database broke down.");
        }
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) {
        if (args.length != 1) {
            return null;
        }
        try {
            return new BodyCommand(Long.parseLong(args[0]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
