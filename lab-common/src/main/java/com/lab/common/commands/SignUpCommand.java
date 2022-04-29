package com.lab.common.commands;

import java.io.IOException;
import java.util.Objects;

import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;
import com.lab.common.util.UserManagerInt;

public class SignUpCommand extends Command {
    private UserManagerInt userCollection;

    public SignUpCommand(UserManagerInt userColl) {
        userCollection = userColl;
    }

    public SignUpCommand() {
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, String userName) {
        User newClient = (User) bodyCommand.getData();
        if (!userCollection.checkIn(newClient)) {
            User authentClient = userCollection.authenticate(newClient);
            if (Objects.nonNull(authentClient)) {
                return new CommandResult("sign up", authentClient, true, "Sign up successfully.");
            }
            return new CommandResult("sign up", authentClient, false, "Something with database went wrong.");
        }
        return new CommandResult("sign up", null, false, "This login is used.");
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
