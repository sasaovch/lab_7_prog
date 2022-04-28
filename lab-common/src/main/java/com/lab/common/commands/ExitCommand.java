package com.lab.common.commands;

import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;

public class ExitCommand extends Command {

    public ExitCommand() {
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, String userName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) {
        ioManager.println("Good Buy!\n\\(?_?)/");
        return new BodyCommand();
    }
}
