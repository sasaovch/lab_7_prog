package com.lab.server.util;

import java.sql.Statement;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lab.common.data.User;
import com.lab.common.util.UserManagerInt;

public class UserManager implements UserManagerInt {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);
    private static final String PEPPER = "^kiU)#320%,";
    private HashSet<String> usersLoginSet;
    private final Connection connectionDB;

    public UserManager(Connection connectionDB) throws SQLException {
        this.connectionDB = connectionDB;
        deSerialize();
    }

    private void deSerialize() throws SQLException {
        usersLoginSet = new HashSet<>();
        Statement stat = connectionDB.createStatement();
        ResultSet res = stat.executeQuery("SELECT login FROM users");
        while (res.next()) {
            usersLoginSet.add(res.getString("login"));
        }
        LOGGER.info("Users collection has been created.");
    }

    private static String encodeHashWithSalt(String message, String salt) {
        try {
            Encoder encoder = Base64.getEncoder();
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest((PEPPER + message + salt).getBytes(StandardCharsets.UTF_8));
            return encoder.encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean checkIn(User client) {
        return usersLoginSet.contains(client.getLogin());
    }

    @Override
    public User authenticate(User client) {
        if (addElement(client)) {
            client.setAuntificationStatusTrue();
            return client;
        }
        return null;
    }

    public boolean addElement(User client) {
        final int saltBytes = 6;
        String insertUser = "INSERT INTO users VALUES ("
                +" ?,?,?) RETURNING login";
        Encoder encoder = Base64.getEncoder();
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltBytes];
        random.nextBytes(salt);
        String saltStr = encoder.encodeToString(salt);
        String hashStr = encodeHashWithSalt(client.getPassword(), saltStr);
        client.setPassword(hashStr);
        client.setSalt(saltStr);
        try (PreparedStatement statUser = connectionDB.prepareStatement(insertUser)) {
            prepareStatUser(statUser, client);
            ResultSet resUser = statUser.executeQuery();
            resUser.next();
            if (resUser.getString("login").equals(client.getLogin())) {
                usersLoginSet.add(client.getLogin());
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.error("Failed to insert element into users database", e);
            return false;
        }
    }
    
    public void prepareStatUser(PreparedStatement stat, User client) throws SQLException {
        int indexColumn = 1;
        stat.setString(indexColumn++, client.getLogin());
        stat.setString(indexColumn++, client.getPassword());
        stat.setString(indexColumn, client.getSalt());
    }

    @Override
    public boolean login(User client) {
        final String findUserQuery = "SELECT * FROM users WHERE login = ?";
        if (usersLoginSet.contains(client.getLogin())) {
            try (PreparedStatement statement = connectionDB.prepareStatement(findUserQuery)) {
                statement.setString(1, client.getLogin());
                ResultSet res = statement.executeQuery();
                res.next();
                String realPasswordHashed = res.getString("password");
                String passwordHashed = encodeHashWithSalt(client.getPassword(), res.getString("salt"));
                if (passwordHashed.equals(realPasswordHashed)) {
                    return true;
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to select element from users database", e);
                return false;
            }
        }
        return false;
    }


    public void disconnect(User client) {
        usersLoginSet.remove(client.getLogin());
    }
}
