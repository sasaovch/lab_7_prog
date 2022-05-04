package com.lab.common.util;

import java.io.Serializable;

import com.lab.common.data.User;

public class Message implements Serializable {
    private static final long serialVersionUID = 2584413675131251528L;
    private User user;
    private String nameCommand;
    private BodyCommand bodyCommand;

    public Message() {
    }

    public Message(String command, BodyCommand bodyCommand) {
        this.nameCommand = command;
        this.bodyCommand = bodyCommand;
    }

    public String getCommand() {
        return nameCommand;
    }

    public BodyCommand getBodyCommand() {
        return bodyCommand;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBodyCommand(BodyCommand bodyCommand) {
        this.bodyCommand = bodyCommand;
    }

    public void setCommand(String command) {
        this.nameCommand = command;
    }
}
