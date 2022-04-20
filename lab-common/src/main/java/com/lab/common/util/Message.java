package com.lab.common.util;

import java.io.Serializable;

import com.lab.common.data.Client;
import com.lab.common.data.SpaceMarine;

public class Message implements Serializable {
    private final Client client;
    private final String command;
    private final Object data;
    private final SpaceMarine spMar;

    public Message(Client client, String command, Object data, SpaceMarine spMar) {
        this.command = command;
        this.data = data;
        this.spMar = spMar;
        this.client = client;
    }

    public String getCommand() {
        return command;
    }

    public Object getData() {
        return data;
    }

    public SpaceMarine getSpacMar() {
        return spMar;
    }

    public Client getClient() {
        return client;
    }
}
