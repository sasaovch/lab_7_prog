package com.lab.common.commands;

import java.io.IOException;
import java.util.Objects;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;
import com.lab.common.util.UserManagerInt;

public class LogInCommand extends Command {
    private UserManagerInt userCollection;

    public LogInCommand(UserManagerInt userColl) {
        userCollection = userColl;
    }

    public LogInCommand() {
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, String userName) {
        User newClient = (User) bodyCommand.getData();
        if (userCollection.checkIn(newClient)) {
            if (userCollection.login(newClient)) {
                newClient.setAuntificationStatusTrue();
                return new CommandResult("log in", newClient, true, "Login successfully.");
            }
            return new CommandResult("log in", null, false, "Failed to log in.");
        }
        return new CommandResult("log in", null, false, "Unknown login. Enter another or sign up.");
    }

    @Override
    public BodyCommand requestBodyCommand(String[] args, IOManager ioManager) throws IOException {
        while (true) {
            ioManager.println("Enter username");
            ioManager.prompt();
            String username = ioManager.readLine();
            if (Objects.isNull(username)) {
                continue;
            }
            if ("".equals(username.trim())) {
                continue;
            }
            ioManager.println("Enter password");
            ioManager.prompt();
            String password = ioManager.readPassword();
            if ("".equals(password.trim())) {
                continue;
            }
            User client = new User(username.trim(), password);
            return new BodyCommand(client);
        }
    }
}
