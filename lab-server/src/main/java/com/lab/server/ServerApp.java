package com.lab.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;

import com.lab.common.commands.Command;
import com.lab.common.commands.CommandManager;
import com.lab.common.commands.CommandResult;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.Message;
import com.lab.server.util.SQLSpMarCollManager;


public class ServerApp {
    private CommandManager commands;
    private SQLSpMarCollManager sqlSpMarCollManager;
    //private UserManager userManager;
    private final Logger logger;
    private final Scanner scanner;
    private Connection connectionDB;
    private SocketAddress client;
    private SocketAddress address;
    private DatagramChannel channel;
    private boolean isWorkState;
    private SendManager sendManager;
    private ReceiveManager receiveManager;

    public ServerApp(InetAddress addr, int port, Connection connDB) throws NumberFormatException, SQLException, IOException {
        this.address = new InetSocketAddress(addr, port);
        this.connectionDB = connDB;
        sqlSpMarCollManager = new SQLSpMarCollManager(connectionDB);
        commands = CommandManager.getDefaultCommandManager(sqlSpMarCollManager);
    }

    {
        isWorkState = true;
        logger = Logger.getLogger("Server");
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException, ClassNotFoundException, InterruptedException, NumberFormatException, SQLException, NoSuchAlgorithmException {
        try (DatagramChannel datachannel = DatagramChannel.open()) {
            this.channel = datachannel;
            logger.info("Open datagram channel. Server started working.");
            logger.info("The collection has been created.");
            //think about exception
            sendManager = new SendManager(channel, client, logger);
            receiveManager = new ReceiveManager(channel, client, logger);
            try {
                channel.bind(address);
                logger.info(channel.getLocalAddress().toString());
            } catch (BindException z) {
                logger.info("Cannot assign requested address.");
                isWorkState = false;
            }
            channel.configureBlocking(false);
            Message mess;
            CommandResult result;
            while (isWorkState) {
                checkCommands();
                mess = receiveManager.receiveMessage();
                if (Objects.isNull(mess)) {
                    continue;
                } else if ("error".equals(mess.getCommand())) {
                    logger.info("Something with data went wrong.");
                    sendManager.setClient(receiveManager.getClient());
                    sendManager.sendCommResult(new CommandResult("error", null, false, "Something with data went wrong. Try again."));
                    continue;
                }
                if (mess.getCommand().equals("exit")) {
                    logger.info("Client disconnected.");
                    continue;
                }
                result = execute(mess);
                sendManager.setClient(receiveManager.getClient());
                sendManager.sendCommResult(result);
            }
        }
    }

    public CommandResult execute(Message mess) throws SQLException {
        Command command = commands.getMap().get(mess.getCommand());
        BodyCommand data = mess.getBodyCommand();
        return command.run(data, null);
    }

    public String readfile(File file) throws FileNotFoundException, IOException {
        StringBuilder strData = new StringBuilder();
        String line;
        if (!file.exists()) {
            throw new FileNotFoundException();
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                while ((line = bufferedReader.readLine()) != null) {
                    strData.append(line);
                }
            }
        }
        return strData.toString();
    }

    public void checkCommands() throws IOException {
        if (System.in.available() > 0) {
            String line = "";
            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                line = "exit";
            }
            if ("exit".equals(line)) {
                logger.info("Server finished working");
                isWorkState = false;
            }
        }
    }
}
