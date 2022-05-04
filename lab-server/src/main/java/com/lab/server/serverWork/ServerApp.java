package com.lab.server.serverWork;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lab.common.commands.Command;
import com.lab.common.commands.CommandManager;
import com.lab.common.commands.CommandResult;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.Message;
import com.lab.common.util.ResultStatusWorkWithColl;
import com.lab.server.util.SQLSpMarCollManager;
import com.lab.server.util.SQLUserManager;


public class ServerApp {
    private static final Logger LOGGER;
    private final Scanner scanner;
    private CommandManager commands;
    private SQLSpMarCollManager sqlSpMarCollManager;
    private SQLUserManager userManager;
    private Connection connectionDB;
    private SocketAddress client;
    private SocketAddress address;
    private DatagramChannel channel;
    private boolean isWorkState;
    private ReceiveManager receiveManager;
    private ExecutorService hanbleMessExecutorService;
    private ExecutorService sendCommandRExecutorService;

    public ServerApp(InetAddress addr, int port, Connection connDB, int numberOfTreads) throws SQLException {
        this.address = new InetSocketAddress(addr, port);
        this.connectionDB = connDB;
        userManager = new SQLUserManager(connectionDB);
        sqlSpMarCollManager = new SQLSpMarCollManager(connectionDB);
        commands = CommandManager.getDefaultCommandManager(sqlSpMarCollManager, userManager);
        hanbleMessExecutorService = Executors.newFixedThreadPool(numberOfTreads);
        sendCommandRExecutorService = Executors.newFixedThreadPool(numberOfTreads);
    }

    {
        isWorkState = true;
        scanner = new Scanner(System.in);
    }

    static {
        LOGGER = LoggerFactory.getLogger(ServerApp.class);
    }

    public void start() throws IOException {
        try (DatagramChannel datachannel = DatagramChannel.open()) {
            this.channel = datachannel;
            LOGGER.info("Open datagram channel. Server started working.");
            receiveManager = new ReceiveManager(channel, client);
            try {
                channel.bind(address);
                LOGGER.info(channel.getLocalAddress().toString());
            } catch (BindException z) {
                LOGGER.error("Cannot assign requested address.", z);
                isWorkState = false;
            }
            channel.configureBlocking(false);
            Message mess;
            while (isWorkState) {
                checkCommands();
                mess = receiveManager.receiveMessage();
                if (Objects.isNull(mess)) {
                    continue;
                }
                new ClientThread(mess, new SendManager(channel, receiveManager.getClient())).start();
            }
        }
    }

    public CommandResult execute(Message mess) {
        Command command = commands.getMap().get(mess.getCommand());
        BodyCommand data = mess.getBodyCommand();
        CommandResult result;
        if (command.requiresAuthen()) {
            ResultStatusWorkWithColl authentication = userManager.authenticate(mess.getUser());
            switch (authentication) {
                case True : result = command.run(data, mess.getUser());
                            break;
                case False : result = new CommandResult(command.getName(), null, false, "User verification failed.");
                            break;
                default : result = new CommandResult(command.getName(), null, false, "Database broke down.");
            }
        } else {
            result = command.run(data, mess.getUser());
        }
        return result;
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
                LOGGER.info("Server finished working.");
                hanbleMessExecutorService.shutdown();
                sendCommandRExecutorService.shutdown();
                isWorkState = false;
            }
        }
    }

    private class ClientThread {
        private final Message mess;
        private final SendManager sendManager;

        ClientThread(Message mess, SendManager sendManager) {
            this.mess = mess;
            this.sendManager = sendManager;
        }

        private void start() {
            try {
                CommandResult commandResult = hanbleMessExecutorService.submit(() -> execute(mess)).get();
                Boolean sendResult = sendCommandRExecutorService.submit(() -> sendManager.sendCommResult(commandResult)).get();
                if (sendResult) {
                   LOGGER.info("Sent message \n-----------------------\n" +  commandResult.getMessageResult() + "\n-----------------------");
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("This thread was damaged.", e);
            }
        }
    }
}
