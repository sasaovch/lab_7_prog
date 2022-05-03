package com.lab.common.commands;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;

public class ExitCommand extends Command {
    @Override
    public CommandResult run(BodyCommand bodyCommand, User client) {
        return new CommandResult("exit", null, true, "Good Buy, " + client.getLogin() + "!\n\\(?_?)/");
    }
}
