package com.lab.common.util;

import java.io.Serializable;

import com.lab.common.data.User;

public class Message implements Serializable {
    private static final long serialVersionUID = 2584413675131251528L;
    private User client;
    private String nameCommand;
    private BodyCommand bodyCommand;

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

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public void setBodyCommand(BodyCommand bodyCommand) {
        this.bodyCommand = bodyCommand;
    }

    public void setCommand(String command) {
        this.nameCommand = command;
    }
}
