package com.lab.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.lab.common.util.ParsFromVR;


public final class Server {
    private static final int DEFAULT_PORT = 8713;

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, SQLException, NumberFormatException, NoSuchAlgorithmException {
        InetAddress address = ParsFromVR.getFromVR("address", InetAddress.getLocalHost(), (variable, defaultVar) -> {
            try {
                return InetAddress.getByName(variable);
            } catch (UnknownHostException e) {
                return defaultVar;
            }
        });
        Integer port = ParsFromVR.getFromVR("port", DEFAULT_PORT, (variable, defaultVar) -> {
            try {
                return Integer.parseInt(variable);
            } catch (NumberFormatException e) {
                return defaultVar;
            }
        });
        String dataBaseHost = ParsFromVR.getFromVR("dbhost", "localhost", (stringHost, defaultValue) -> stringHost);
        String dataBaseTable = ParsFromVR.getFromVR("dbhost", "lab", (stringTable, defaultValue) -> stringTable);
        String dataBaseUser = ParsFromVR.getFromVR("dbuser", "postgres", (stringUser, defaultValue) -> stringUser);
        String dataBasePassword = ParsFromVR.getFromVR("dbhost", "87740432164", (stringPassword, defaultValue) -> stringPassword);
        try (Connection connectionDB = DriverManager.getConnection(
            "jdbc:postgresql://" + dataBaseHost + '/' + dataBaseTable,
            dataBaseUser,
            dataBasePassword
        )) {
            ServerApp app = new ServerApp(address, port, connectionDB);
            app.start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
