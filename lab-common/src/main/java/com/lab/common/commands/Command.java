package com.lab.common.commands;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;

import java.io.IOException;


public abstract class Command {

    public abstract CommandResult run(BodyCommand bodyCommand, User client);

    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) throws IOException {
        if (args.length != 0) {
            return null;
        }
        return new BodyCommand();
    }
}
