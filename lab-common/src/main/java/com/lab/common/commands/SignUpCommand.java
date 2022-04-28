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
        while (true) {// why while true?
            ioManager.println("Enter username");
            ioManager.prompt();
            String username = ioManager.readLine().trim();
            if (username.equals("")) {
                continue;
            }
            ioManager.println("Enter password");
            ioManager.prompt();
            String password = ioManager.readPassword(); //check empty line and spaces at the end
            if (password.trim().equals("")) {
                continue;
            }
            User client = new User(username, password);
            return new BodyCommand(client);
        }
    }
}
