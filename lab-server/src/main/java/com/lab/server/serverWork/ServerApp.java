package com.lab.server.serverWork;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lab.common.commands.Command;
import com.lab.common.commands.CommandManager;
import com.lab.common.commands.CommandResult;
import com.lab.common.util.BodyCommand;
import com.lab.common.util.Message;
import com.lab.server.util.SQLSpMarCollManager;
import com.lab.server.util.UserManager;


public class ServerApp {
    private static final Logger LOGGER;
    private final Scanner scanner;
    private CommandManager commands;
    private SQLSpMarCollManager sqlSpMarCollManager;
    private UserManager userManager;
    private Connection connectionDB;
    private SocketAddress client;
    private SocketAddress address;
    private DatagramChannel channel;
    private boolean isWorkState;
    private ReceiveManager receiveManager;
    private ExecutorService executorService;

    public ServerApp(InetAddress addr, int port, Connection connDB, int numberOfTreads) throws SQLException {
        this.address = new InetSocketAddress(addr, port);
        this.connectionDB = connDB;
        sqlSpMarCollManager = new SQLSpMarCollManager(connectionDB);
        userManager = new UserManager(connectionDB);
        commands = CommandManager.getDefaultCommandManager(sqlSpMarCollManager, userManager);
        executorService = Executors.newFixedThreadPool(numberOfTreads);
    }

    {
        isWorkState = true;
        scanner = new Scanner(System.in);
    }

    static {
        LOGGER = LoggerFactory.getLogger(ServerApp.class);
    }

    public void start() throws IOException, ClassNotFoundException, InterruptedException, NumberFormatException, SQLException, NoSuchAlgorithmException {
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
                executorService.submit(new ClientThread(new SendManager(channel, receiveManager.getClient()), mess));
            }
        }
    }

    public CommandResult execute(Message mess) throws SQLException {
        Command command = commands.getMap().get(mess.getCommand());
        BodyCommand data = mess.getBodyCommand();
        return command.run(data, mess.getClient().getLogin());
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
                executorService.shutdown();
                isWorkState = false;
            }
        }
    }

    private class ClientThread implements Runnable {
        private final SendManager sendManager;
        private final Message mess;

        ClientThread(SendManager socket, Message mess) {
            this.sendManager = socket;
            this.mess = mess;
        }

        @Override
        public void run() {
            try {
                if ("error".equals(mess.getCommand())) {
                    LOGGER.info("Something with data went wrong.");
                    sendManager.sendCommResult(new CommandResult("error", null, false, "Something with data went wrong. Try again."));
                }
                if (mess.getCommand().equals("exit")) {
                    LOGGER.info("Client disconnected.");
                    userManager.disconnect(mess.getClient());
                }
                CommandResult result = execute(mess);
                sendManager.sendCommResult(result);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
