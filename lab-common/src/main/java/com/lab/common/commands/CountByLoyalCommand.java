package com.lab.common.commands;

import com.lab.common.data.SpaceMarine;
import com.lab.common.util.CollectionManager;

public class CountByLoyalCommand extends Command {

    @Override
    public CommandResult run(Object data, SpaceMarine spMar, CollectionManager collection) {
        Integer count = collection.countBySomeThing(SpaceMarine::getLoyal, (Boolean) data);
        return new CommandResult("count_by_loyal", count, true);
    }
}
