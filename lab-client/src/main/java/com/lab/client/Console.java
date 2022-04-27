package com.lab.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

//import com.lab.common.commands.AuthenicateCommand;
import com.lab.common.commands.Command;
import com.lab.common.commands.CommandManager;
import com.lab.common.commands.CommandResult;
// import com.lab.common.commands.LogInCommand;
// import com.lab.common.commands.SignUpCommand;
import com.lab.common.data.User;
import com.lab.common.exception.CommandArgumentException;
import com.lab.common.exception.IncorrectData;
import com.lab.common.exception.IncorrectDataOfFileException;
import com.lab.common.util.AskerInformation;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.IOManager;
import com.lab.common.util.Message;


public class Console {
    private final IOManager ioManager;
    private final ReceiveManager receiveManager;
    private final SendManager sendManager;
    private Message message = new Message();
    private Boolean isWorkState = true;
    private CommandManager commandManager = CommandManager.getDefaultCommandManager(null);
    private User client = new User();

    public Console(IOManager ioManager, ReceiveManager receiveManager, SendManager sendManager) {
        this.ioManager = ioManager;
        this.receiveManager = receiveManager;
        this.sendManager = sendManager;
    }

    public void run() throws IOException, IncorrectData, ClassNotFoundException, IncorrectDataOfFileException, InterruptedException, CommandArgumentException {
        String line = "";
        String nameCommand;
        String[] value;
        String[] commandline;
        //client = authenticate();
        //message.setClient(client);
        while (isWorkState) {
            if (!ioManager.getFileMode()) {
                ioManager.prompt();
            }
            line = ioManager.readLine();
            if (!checkLine(line)) {
                continue;
            }
            commandline = (line.trim() + " " + " ").split(" ");
            nameCommand = commandline[0];
            value = Arrays.copyOfRange(commandline, 1, commandline.length);   
            Command command = parsCommand(nameCommand);
            if (Objects.nonNull(command)) {
                try {
                    BodyCommand bodyCommand = command.requestBodyCommand(value, ioManager);
                    if (Objects.nonNull(bodyCommand)) {
                        message.setBodyCommand(bodyCommand);
                        message.setCommand(nameCommand);
                        sendManager.sendMessage(message);
                        checkAndPrintResult(receiveManager.reciveMessage());
                    } else {
                        ioManager.printerr("Incorrect input. Enter 'help' to view correct input");
                    }
                } catch (CommandArgumentException e) {
                    ioManager.printerr("Incorrect input.");
                }
            }         
        }
        ioManager.println("Good Buy!\n\\(?_?)/");
    }

    // public User authenticate() throws IOException, IncorrectDataOfFileException, ClassNotFoundException, InterruptedException {
    //     while (true) {
    //         //int askTypeOfAuthin = AskerInformation.askTypeOfAuthin(ioManager);
    //         //AuthenicateCommand authenicateCommand = new AuthenicateCommand();
    //         BodyCommand bodyCommand = authenicateCommand.requestBodyCommand(null, ioManager);
    //         if (askTypeOfAuthin == 1) {
    //             message = new Message(new LogInCommand(), bodyCommand);
    //         } else {
    //             message = new Message(new SignUpCommand(), bodyCommand);
    //         }
    //         sendManager.sendMessage(message);
    //         CommandResult commandResult = receiveManager.reciveMessage();
    //         if (commandResult.getResultStatus()) {return (User) commandResult.getData();}
    //         ioManager.printerr("Login or password is not correct.\nTry again");
    //     }
    // }

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

    public boolean checkLine(String line) {
        if ((Objects.isNull(line) && !ioManager.getFileMode()) || ("".equals(line.trim()) && !ioManager.getFileMode())) {
            return false;
        } else if ((Objects.isNull(line)) || ("".equals(line.trim()))) {
            ioManager.turnOffFileMode();
            return false;
        }
        return true;
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
