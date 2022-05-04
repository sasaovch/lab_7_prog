package com.lab.common.commands;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;

public class RemoveByIdCommand extends Command {
    private CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collection) {
        super("remove_by_id", "remove_by_id id : remove element by its id");
        collectionManager = collection;
    }


    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        switch (collectionManager.removeIf(spMar -> (spMar.getID().equals(bodyCommand.getData()) && (spMar.getOwnerName().equals(user.getLogin()))))) {
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
