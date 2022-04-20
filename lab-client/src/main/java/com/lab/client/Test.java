package com.lab.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/lab", "postgres", "87740432164");
        Statement stat = conn.createStatement();
        ResultSet res = stat.executeQuery("SELECT * FROM test");
    }
}
