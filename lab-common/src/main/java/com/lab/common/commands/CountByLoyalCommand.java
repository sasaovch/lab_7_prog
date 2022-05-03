package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.CollectionManager;
import com.lab.common.util.IOManager;

public class CountByLoyalCommand extends Command {
    private CollectionManager collectionManager;

    public CountByLoyalCommand() {
    }

    public CountByLoyalCommand(CollectionManager collection) {
        collectionManager = collection;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User client) {
        Integer count = collectionManager.countBySomeThing(SpaceMarine::getLoyal, (Boolean) bodyCommand.getData());
        return new CommandResult("count_by_loyal", count, true, "Count by loyal - " + (Boolean) bodyCommand.getData() + ": " + count);
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) {
        if (args.length == 0) {
            return new BodyCommand(null);
        } else if (args.length == 1) {
            if (args[0].equals("true") || args[0].equals("false")) {
                return new BodyCommand(Boolean.parseBoolean(args[0]));
            }
        }
        return null;
    }
}
