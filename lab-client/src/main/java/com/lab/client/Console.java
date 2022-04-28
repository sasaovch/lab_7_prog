package com.lab.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.lab.common.commands.Command;
import com.lab.common.commands.CommandManager;
import com.lab.common.commands.CommandResult;
import com.lab.common.data.User;
import com.lab.common.util.AskerInformation;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;
import com.lab.common.util.Message;


public class Console {
    private final IOManager ioManager;
    private final ReceiveManager receiveManager;
    private final SendManager sendManager;
    private final CommandManager commandManager = CommandManager.getDefaultCommandManager(null, null);
    private User client;
    private Message message;
    private boolean isWorkState = true;

    public Console(IOManager ioManager, ReceiveManager receiveManager, SendManager sendManager) {
        this.ioManager = ioManager;
        this.receiveManager = receiveManager;
        this.sendManager = sendManager;
    }

    public void run() throws IOException {
        String line = "";
        String nameCommand;
        String[] value;
        String[] commandline;
        authenticateClient();
        while (isWorkState) {
            if (!ioManager.getFileMode()) {
                ioManager.prompt();
            }
            line = ioManager.readLine();
            if (checkEmptyLine(line)) {
                continue;
            }
            commandline = (line + " " + " ").split(" ");
            nameCommand = commandline[0];
            value = Arrays.copyOfRange(commandline, 1, commandline.length);   
            Command command = parsCommand(nameCommand);
            if (Objects.nonNull(command)) {
                BodyCommand bodyCommand = command.requestBodyCommand(value, ioManager);
                if (Objects.nonNull(bodyCommand)) {
                    message.setCommand(nameCommand);
                    message.setBodyCommand(bodyCommand);
                    sendManager.sendMessage(message);
                    checkAndPrintResult(receiveManager.receiveMessage());
                } else if (!nameCommand.equals("execute_script")){
                    ioManager.printerr("Incorrect arguments in command. Enter 'help' to view correct arguments.");
                }
            }
        }
    }

    public void authenticateClient() throws IOException {
        while (true) {
            int askTypeOfAuthen = AskerInformation.askTypeOfAuthen(ioManager);
            BodyCommand bodyCommand;
            if (askTypeOfAuthen == 1) {
                bodyCommand = commandManager.getCommand("log in").requestBodyCommand(null, ioManager);
                message = new Message("log in", bodyCommand);
            } else if (askTypeOfAuthen == 2) {
                bodyCommand = commandManager.getCommand("sign up").requestBodyCommand(null, ioManager);
                message = new Message("sign up", bodyCommand);
            } else {
                isWorkState = false;
                return;
            }
            message.setClient((User)bodyCommand.getData());
            sendManager.sendMessage(message);
            CommandResult commandResult = receiveManager.receiveMessage(); //what I receive, trobles when server disconnecting
            if (Objects.nonNull(commandResult)) {
                if (commandResult.getResultStatus()) {
                    client = (User) commandResult.getData();
                    message.setClient(client);
                    ioManager.println("Welcome, " + client.getLogin() + "!");
                    return;
                }
                ioManager.printerr(commandResult.getMessageResult());
                continue;
            }
            ioManager.printerr("Failed to connect server.");
        }
    }

    public Command parsCommand(String name) {
        Command command = commandManager.getCommand(name);
        if (Objects.isNull(command)) {
            if (!ioManager.getFileMode()) {
                ioManager.printerr("Unknown commands. Print help for getting info about commands");
                return null;
            } else {
                ioManager.printerr("Unknow command in file.");
                ioManager.turnOffFileMode();
                return null;
            }
        }
        if (name.equals("exit")) {
            isWorkState = false;
        }
        return command;
    }

    public boolean checkEmptyLine(String line) {
        if (Objects.isNull(line) && !ioManager.getFileMode()) {
            return true;
        } else if (Objects.isNull(line)) {
            ioManager.turnOffFileMode();
            return true;
        } else if ("".equals(line) && !ioManager.getFileMode()) {
            return true;
        } else if ("".equals(line)) {
            ioManager.turnOffFileMode();
            return true;
        }
        return false;
    }

    public void checkAndPrintResult(CommandResult result) {
        if (Objects.isNull(result) && isWorkState) {
            ioManager.println("Hmm... Something with server went wrong. Try again later.");
        } else if (Objects.isNull(result)) {
            return;
        } else if (result.getResultStatus()) {
            ioManager.println(result.getMessageResult());
        } else {
            ioManager.printerr(result.getMessageResult());
        }
    }
}
