package com.lab.server.serverWork;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.lab.common.util.ParsFromEV;


public final class Server {
    private static final int DEFAULT_PORT = 8713;
    private static final int DEFAULT_NUMBER_OF_THREADS = 5;

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException, NumberFormatException, NoSuchAlgorithmException {
        try {
            InetAddress address = ParsFromEV.getFromEV("address", InetAddress.getLocalHost(), (variable, defaultVar) -> {
                try {
                    return InetAddress.getByName(variable);
                } catch (UnknownHostException e) {
                    return defaultVar;
                }
            });
            int port = ParsFromEV.getFromEV("port", DEFAULT_PORT, (variable, defaultVar) -> {
                try {
                    return Integer.parseInt(variable);
                } catch (NumberFormatException e) {
                    return defaultVar;
                }
            });
            int numberOfTreads = ParsFromEV.getFromEV("numberOfThreads", DEFAULT_NUMBER_OF_THREADS, (variable, defaultVar) -> {
                try {
                    return Integer.parseInt(variable);
                } catch (NumberFormatException e) {
                    return defaultVar;
                }
            });
            // String dataBaseHost = ParsFromEV.getFromEV("dbhost", "pg", (stringHost, defaultValue) -> stringHost);
            // String dataBaseTable = ParsFromEV.getFromEV("dbtable", "studs", (stringTable, defaultValue) -> stringTable);
            // String dataBaseUser = ParsFromEV.getFromEV("dbuser", "s336768", (stringUser, defaultValue) -> stringUser);
            // String dataBasePassword = ParsFromEV.getFromEV("dbpassword", "ccw507", (stringPassword, defaultValue) -> stringPassword);
            String dataBaseHost = ParsFromEV.getFromEV("dbhost", "localhost", (stringHost, defaultValue) -> stringHost);
            String dataBaseTable = ParsFromEV.getFromEV("dbtable", "lab", (stringTable, defaultValue) -> stringTable);
            String dataBaseUser = ParsFromEV.getFromEV("dbuser", "postgres", (stringUser, defaultValue) -> stringUser);
            String dataBasePassword = ParsFromEV.getFromEV("dbpassword", "87740432164", (stringPassword, defaultValue) -> stringPassword);
            try (Connection connectionDB = DriverManager.getConnection("jdbc:postgresql://" + dataBaseHost + '/' + dataBaseTable, dataBaseUser, dataBasePassword)) {
                    ServerApp app = new ServerApp(address, port, connectionDB, numberOfTreads);
                    app.start();
                } catch (SQLException e) {
                    System.out.println("Failed to connect to postresql or another error.\n");
                    System.out.println("Use environment variables: address, port, numberOfThreads, dbhost, dbtable, dbuser, dbpassword.\n");
                    e.printStackTrace();
                }
        } catch (IOException e) {
            System.out.println("Uppss...");
            e.printStackTrace();
        }
    }
}
