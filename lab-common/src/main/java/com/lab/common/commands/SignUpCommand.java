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
        super("sign_up", "sign_up {user} : register new user", false);
        userCollection = userColl;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        User newClient = (User) bodyCommand.getData();
        if (!userCollection.checkIn(newClient)) {
            User authentClient = userCollection.register(newClient);
            if (Objects.nonNull(authentClient)) {
                return new CommandResult("sign_up", authentClient, true, "Registration completed successfully.");
            }
            return new CommandResult("sign_up", null, false, "Something with database went wrong.");
        }
        return new CommandResult("sign_up", null, false, "This username is used.");
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
            User user = new User(username.trim(), password);
            return new BodyCommand(user);
        }
    }
}
