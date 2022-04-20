package com.lab.client;

import java.io.IOException;

import com.lab.common.data.Client;
import com.lab.common.data.SpaceMarine;
import com.lab.common.exception.IncorrectData;
import com.lab.common.exception.IncorrectDataOfFileException;
import com.lab.common.util.Message;

public class ParsCommand {
    private final IOManager ioManager;
    private final AskMarine asker;
    private Client client;

    public ParsCommand(IOManager ioMan, AskMarine askr) {
        ioManager = ioMan;
        asker = askr;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Message addComm(String value) {
        SpaceMarine spMar = asker.askMarine();
        Message message = new Message(client,"add", null, spMar);
        return message;
    }

    public Message updateComm(String value) {
        Long id;
                try {
                    id = Long.parseLong(value);
                } catch (NumberFormatException e) {
                    ioManager.printerr("Incorrect data");
                    return null;
                }
                SpaceMarine spMar = asker.askMarine();
                Message message = new Message(client, "update", id, spMar);
                return message;
    }

    public Message removeByIdComm(String value) {
        Long id;
        try {
            id = Long.parseLong(value);
        } catch (NumberFormatException e) {
            ioManager.printerr("Incorrect data");
            return null;
        }
        Message message = new Message(client, "remove_by_id", id, null);
        return message;
    }

    public Message addIfMinComm(String value) {
        SpaceMarine spMar = asker.askMarine();
        Message message = new Message(client, "add_if_min", null, spMar);
        return message;
    }

    public Message removeGreaterComm(String value) {
        try {
            Integer health = asker.askHealth();
            Integer heart = asker.askHeartCount();
            SpaceMarine spMar = new SpaceMarine();
            spMar.setHealth(health);
            spMar.setHeartCount(heart);
            Message message = new Message(client, "remove_greater", null, spMar);
            return message;
        } catch (IOException | IncorrectDataOfFileException | IncorrectData e) {
            return null;
        }
    }

    public Message removeLowerComm(String value) {
        try {
            Integer health = asker.askHealth();
            Integer heart = asker.askHeartCount();
            SpaceMarine spMar = new SpaceMarine();
            spMar.setHealth(health);
            spMar.setHeartCount(heart);
            Message message = new Message(client, "remove_lower", null, spMar);
            return message;
        } catch (IOException | IncorrectDataOfFileException | IncorrectData e) {
            return null;
        }
    }

    public Message countByLoyalComm(String value) {
        Boolean loyal;
        if ("".equals(value)) {
            loyal = null;
        } else if (!("true".equals(value) || "false".equals(value))) {
            ioManager.printerr("The value of Loyal isn't correct (true, false, null - empty line)");
            return null;
        } else {
            loyal = Boolean.parseBoolean(value);
        }
        Message message = new Message(client, "count_by_loyal", loyal, null);
        return message;
    }

    public Message executeScriptComm(String value) {
        ioManager.turnOnFileMode(value);
        return new Message(client, "execute", null, null);
    }

    public Message helpComm(String value) {
        return new Message(client, "help", null, null);
    }

    public Message infoComm(String value) {
        return new Message(client, "info", null, null);
    }

    public Message showComm(String value) {
        return new Message(client, "show", null, null);
    }

    public Message clearComm(String value) {
        return new Message(client, "clear", null, null);
    }

    public Message exitComm(String value) {
        return new Message(client, "exit", null, null);
    }

    public Message groupCountingByNameComm(String value) {
        return new Message(client, "group_counting_by_name", null, null);
    }

    public Message printDescendingComm(String value) {
        return new Message(client, "print_descending", null, null);
    }
}
