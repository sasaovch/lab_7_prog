package com.lab.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.lab.common.commands.Command;
import com.lab.common.commands.CommandManager;
import com.lab.common.commands.CommandResult;
import com.lab.common.data.User;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;
import com.lab.common.util.Message;


public class Console {
    private final IOManager ioManager;
    private final ReceiveManager receiveManager;
    private final SendManager sendManager;
    private final CommandManager commandManager = CommandManager.getDefaultCommandManager(null, null);
    private Message message;
    private boolean isWorkState = true;

    public Console(IOManager ioManager, ReceiveManager receiveManager, SendManager sendManager) {
        this.ioManager = ioManager;
        this.receiveManager = receiveManager;
        this.sendManager = sendManager;
        message = new Message();
    }

    public void run() throws IOException {
        String line = "";
        String nameCommand;
        String[] value;
        String[] commandline;
        ioManager.println("Hello! Authenticate or register, please!\nEnter command 'login' or 'sign_up'");
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
                } else if (!"execute_script".equals(nameCommand)) {
                    // print errors in parsing command
                    ioManager.printerr("Incorrect arguments in command. Enter 'help' to view correct arguments.");
                }
            }
        }
    }

    public Command parsCommand(String name) {
        Command command = commandManager.getCommand(name);
        if (Objects.isNull(command)) {
            if (ioManager.getFileMode()) {
                ioManager.printerr("Unknow command in file.");
                ioManager.turnOffFileMode();
                return null;
            } else {
                ioManager.printerr("Unknown commands. Print help for getting info about commands");
                return null;
            }
        }
        if ("exit".equals(name)) {
            isWorkState = false;
        }
        return command;
    }

    public boolean checkEmptyLine(String line) {
        boolean returnStatement = false;
        if (Objects.isNull(line) && !ioManager.getFileMode()) {
            returnStatement = true;
        } else if (Objects.isNull(line)) {
            ioManager.turnOffFileMode();
            returnStatement = true;
        } else if ("".equals(line.trim()) && !ioManager.getFileMode()) {
            returnStatement = true;
        } else if ("".equals(line.trim())) {
            ioManager.turnOffFileMode();
            returnStatement = true;
        }
        return returnStatement;
    }

    public void checkAndPrintResult(CommandResult result) {
        if (Objects.isNull(result) && isWorkState) {
            ioManager.println("Hmm... Something with server went wrong. Try again later.");
        } else if (Objects.isNull(result)) {
            return;
        } else if (isAuthenCommandWithClient(result)) {
            message.setUser((User) result.getData());
            ioManager.println("Welcome, " + message.getUser().getUsername() + "!");
        } else if (result.getResultStatus()) {
            ioManager.println(result.getMessageResult());
        } else {
            ioManager.printerr(result.getMessageResult());
        }
    }

    private boolean isAuthenCommandWithClient(CommandResult result) {
        return (!commandManager.getCommand(result.getCommandName()).requiresAuthen() && result.getResultStatus());
    }
}
