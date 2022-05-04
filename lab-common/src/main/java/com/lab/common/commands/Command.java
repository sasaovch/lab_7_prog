package com.lab.common.commands;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;

import java.io.IOException;


public abstract class Command {
    private final String name;
    private final String description;
    private final boolean requireAuthen;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
        requireAuthen = true;
    }

    public Command(String name, String description, boolean requireAuthen) {
        this.name = name;
        this.description = description;
        this.requireAuthen = requireAuthen;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresAuthen() {
        return requireAuthen;
    }


    public abstract CommandResult run(BodyCommand bodyCommand, User user);

    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) throws IOException {
        if (args.length != 0) {
            return null;
        }
        return new BodyCommand();
    }
}
