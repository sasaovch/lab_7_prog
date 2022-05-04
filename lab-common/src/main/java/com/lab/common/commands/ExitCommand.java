package com.lab.common.commands;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;

public class ExitCommand extends Command {

    public ExitCommand() {
        super("exit", "exit : shut down the work", true);
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        return new CommandResult("exit", null, true, "Good Buy, " + user.getUsername() + "!\n\\(?_?)/");
    }
}
