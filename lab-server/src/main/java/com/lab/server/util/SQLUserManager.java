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
import java.util.Objects;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Base64.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lab.common.data.User;
import com.lab.common.util.ResultStatusWorkWithColl;
import com.lab.common.util.UserManagerInt;

public class SQLUserManager implements UserManagerInt {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLUserManager.class);
    private static final String PEPPER = "^kiU)#320%,";
    private Collection<String> usersLoginSet;
    private final Connection connectionDB;

    public SQLUserManager(Connection connectionDB) throws SQLException {
        this.connectionDB = connectionDB;
        CreateSQLTable.createUserTable(connectionDB);
        deSerialize();
    }

    private void deSerialize() throws SQLException {
        usersLoginSet = Collections.synchronizedSet(new HashSet<>());
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
    public boolean checkIn(User user) {
        return usersLoginSet.contains(user.getUsername());
    }

    @Override
    public User register(User user) {
        if (addElement(user).equals(ResultStatusWorkWithColl.True)) {
            user.setAuntificationStatusTrue();
            return user;
        }
        return null;
    }

    public ResultStatusWorkWithColl addElement(User user) {
        final int saltBytes = 7;
        final int indexPassword = 2;
        String insertUser = "INSERT INTO users VALUES ("
                + " ?,?,?) RETURNING login";
        Encoder encoder = Base64.getEncoder();
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltBytes];
        random.nextBytes(salt);
        String saltStr = encoder.encodeToString(salt);
        String hashStr = encodeHashWithSalt(user.getPassword(), saltStr);
        user.setSalt(saltStr);
        try (PreparedStatement statUser = connectionDB.prepareStatement(insertUser)) {
            prepareStatUser(statUser, user);
            statUser.setString(indexPassword, hashStr);
            ResultSet resUser = statUser.executeQuery();
            resUser.next();
            if (resUser.getString("login").equals(user.getUsername())) {
                usersLoginSet.add(user.getUsername());
                return ResultStatusWorkWithColl.True;
            }
            return ResultStatusWorkWithColl.False;
        } catch (SQLException e) {
            LOGGER.error("Failed to insert element into users database", e);
            return ResultStatusWorkWithColl.Error;
        }
    }

    public void prepareStatUser(PreparedStatement stat, User user) throws SQLException {
        int indexColumn = 1;
        stat.setString(indexColumn++, user.getUsername());
        stat.setString(indexColumn++, user.getPassword());
        stat.setString(indexColumn, user.getSalt());
    }

    @Override
    public ResultStatusWorkWithColl authenticate(User user) {
        final String findUserQuery = "SELECT * FROM users WHERE login = ?";
        if (Objects.isNull(user)) {
            return ResultStatusWorkWithColl.False;
        }
        if (usersLoginSet.contains(user.getUsername())) {
            try (PreparedStatement statement = connectionDB.prepareStatement(findUserQuery)) {
                statement.setString(1, user.getUsername());
                ResultSet res = statement.executeQuery();
                res.next();
                String realPasswordHashed = res.getString("password");
                String passwordHashed = encodeHashWithSalt(user.getPassword(), res.getString("salt"));
                if (passwordHashed.equals(realPasswordHashed)) {
                    return ResultStatusWorkWithColl.True;
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to select element from users database", e);
                return ResultStatusWorkWithColl.Error;
            }
        }
        return ResultStatusWorkWithColl.False;
    }
}
