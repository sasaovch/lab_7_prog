package com.lab.common.commands;

import com.lab.common.exception.CommandArgumentException;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;

import java.io.IOException;


public abstract class Command {
    
    public abstract CommandResult run(BodyCommand bodyCommand, Long userID);

    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) throws CommandArgumentException, IOException {
        if (args.length != 0) {
            throw new CommandArgumentException(); // catch commandArgumentException
        }
        return new BodyCommand();
    }
} 
