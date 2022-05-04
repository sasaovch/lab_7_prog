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
        super("login", "login {user} : authenticate user", false);
        userCollection = userColl;
    }

    @Override
    public CommandResult run(BodyCommand bodyCommand, User user) {
        User newClient = (User) bodyCommand.getData();
        if (userCollection.checkIn(newClient)) {
            switch (userCollection.authenticate(newClient)) {
                case True : newClient.setAuntificationStatusTrue();
                            return new CommandResult("login", newClient, true, "Authentication completed successfully.");
                case False : return new CommandResult("login", null, false, "Authentication failed.");
                default :
                    return new CommandResult("login", null, false, "Database broke down.");
            }
        }
        return new CommandResult("login", null, false, "Unknown username. Enter another or sign up.");
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
