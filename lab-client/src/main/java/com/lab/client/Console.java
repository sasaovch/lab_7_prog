package com.lab.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;

import com.lab.common.commands.CommandResult;
import com.lab.common.data.Client;
import com.lab.common.data.SpaceMarine;
import com.lab.common.exception.IncorrectData;
import com.lab.common.exception.IncorrectDataOfFileException;
import com.lab.common.util.Message;


public class Console {
    private final Map<String, Function<String, Message>> parsingList;
    private final IOManager ioManager;
    private final ReceiveManager receiveManager;
    private final SendManager sendManager;
    private final Integer lengthOfCommand = 3;
    private Message message;
    private Boolean isWorkState = true;

    public Console(Map<String, Function<String, Message>> parsingList, IOManager ioManager, ReceiveManager receiveManager, SendManager sendManager) {
        this.parsingList = parsingList;
        this.ioManager = ioManager;
        this.receiveManager = receiveManager;
        this.sendManager = sendManager;
    }

    public void run() throws IOException, IncorrectData, ClassNotFoundException, IncorrectDataOfFileException, InterruptedException {
        String[] command = new String[lengthOfCommand];
        String line = "";
        String name;
        String value;
        String lastPartComm;
        String username;
        String password;
        Client client;
        while (true) {
            ioManager.println("Do you want to sign in or sign up?");
            ioManager.println(("Enter '1' for sign in, '2' for sign up"));
            int option;
            try {
                option = Integer.parseInt(ioManager.readLine().trim());
            } catch (NumberFormatException e) {
                ioManager.printerr("It's not an Integer");
                continue;
            }
            ioManager.println("Enter username");
            ioManager.prompt();
            username = ioManager.readLine().trim();
            ioManager.println("Enter password");
            ioManager.prompt();
            password = ioManager.readPassword(); //check empty line and spaces at the end
            client = new Client(username, password);
            if (option == 1) {
                sendManager.sendMessage(new Message(client, "authenication", null, null));
                if (receiveManager.reciveMessage().getResult()) {
                    ioManager.println("Welcome " + username);
                    break;
                } else {
                    ioManager.printerr("Incorrect username or password");
                    continue;
                }
            } else if (option == 2) {
                sendManager.sendMessage(new Message(client, "registration", null, null));
                if (receiveManager.reciveMessage().getResult()) {
                    ioManager.println("Registration was successful. Welcom " + username);
                    break;
                } else {
                    ioManager.printerr("Sorry, this username is already taken. Try another");
                    continue;
                }
            } else {
                ioManager.printerr("Only 1 or 2 is supperted.");
                continue;
            }
        }
        while (isWorkState) {
            if (!ioManager.getFileMode()) {
                ioManager.prompt();
            }
            line = ioManager.readLine();
            if (!checkLine(line)) {
                continue;
            }
            command = (line.trim() + " " + " ").split(" ", lengthOfCommand);
            name = command[0];
            value = command[1];
            lastPartComm = command[2];
            if (!checkCommand(name, lastPartComm)) {
                continue;
            }
            if (parsCommand(name, value)) {
                sendManager.sendMessage(message);
                checkAndPrintResult(receiveManager.reciveMessage());
            }
        }
        ioManager.println("Good Buy!\n\\(?_?)/");
    }

    public boolean parsCommand(String name, String value) {
            Function<String, Message> func = parsingList.get(name);
            message = func.apply(value);
            if (Objects.isNull(message)) {
                if (ioManager.getFileMode()) {
                    ioManager.turnOffFileMode();
                    return false;
                } else {
                    return false;
                }
            } else if ("execute".equals(message.getCommand())) {
                return false;
            } else if ("exit".equals(message.getCommand())) {
                isWorkState = false;
            }
            return true;
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

    public boolean checkCommand(String name, String lastPartComm) {
        boolean resultcheck = true;
        if ("".equals(name) && !ioManager.getFileMode()) {
            resultcheck = false;
        } else if ("".equals(name)) {
            ioManager.turnOffFileMode();
            resultcheck = false;
        }
        if (Objects.isNull(parsingList.get(name))) {
            if (!ioManager.getFileMode()) {
                ioManager.printerr("Unknown commands. Print help for getting info about commands");
                resultcheck = false;
            } else {
                ioManager.printerr("Unknow command in file.");
                ioManager.turnOffFileMode();
                resultcheck = false;
            }
        }
        if (!"".equals(lastPartComm.trim())) {
            ioManager.printerr("Incorrect input.");
            resultcheck = false;
        }
        return resultcheck;
    }

    public void checkAndPrintResult(CommandResult result) {
        if (Objects.isNull(result) && isWorkState) {
            ioManager.println("Hmm... Something with server went wrong. Try again later.");
        } else if (Objects.isNull(result)) {
            return;
        } else if (result.getResult()) {
            switch (result.getName()) {
                case "count_by_loyal" :
                    ioManager.println("Count by loyal: " + result.getData());
                    break;
                case "group_counting_by_name" :
                    @SuppressWarnings("unchecked") Map<String, List<SpaceMarine>> outMap = (TreeMap<String, List<SpaceMarine>>) result.getData();
                    ioManager.println("Group counting by name");
                    outMap.entrySet().stream().forEach(s -> ioManager.println(s.getKey() + ": " + s.getValue().size()));
                    break;
                case "info" :
                    @SuppressWarnings("unchecked") Map<String, Object> printMap = (TreeMap<String, Object>) result.getData();
                    ioManager.println("Info");
                    printMap.entrySet().stream().forEach(s -> ioManager.println(s.getKey() + ": " + s.getValue()));
                    break;
                default :
                    ioManager.println(result.getData());
            }
        } else {
            ioManager.printerr(result.getData());
        }
    }
}
