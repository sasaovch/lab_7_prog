package com.lab.common.commands;


import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;

public class ExecuteScriptCommand extends Command {

    @Override
    public CommandResult run(BodyCommand bodyCommand, User client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) {
        if (args.length != 1) {
            return null;
        }
        ioManager.turnOnFileMode(args[0]);
        return null;
    }
}
