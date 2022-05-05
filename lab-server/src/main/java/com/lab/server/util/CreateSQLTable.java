package com.lab.server.util;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class CreateSQLTable {

    private CreateSQLTable() {
    }

    static void createUserTable(Connection connectionDB) throws SQLException {
        final String createTableQuery = "CREATE TABLE IF NOT EXISTS users ("
                                            + " login VARCHAR(50)   PRIMARY KEY,"
                                            + " password VARCHAR NOT NULL,"
                                            + " salt VARCHAR NOT NULL)";
        Statement stat = connectionDB.createStatement();
        stat.execute(createTableQuery);
    }

    static void createCoordTable(Connection connectionDB) throws SQLException {
        final String createTableQuery = "CREATE TABLE IF NOT EXISTS coordinates ("
                                        + " id SERIAL PRIMARY KEY,"
                                        + " x double PRECISION,"
                                        + " y BIGINT);";
        Statement stat = connectionDB.createStatement();
        stat.execute(createTableQuery);
    }

    static void createChapterTable(Connection connectionDB) throws SQLException {
        final String createTableQuery = "CREATE TABLE IF NOT EXISTS chapter ("
                                        + " id SERIAL PRIMARY KEY,"
                                        + " name VARCHAR(50),"
                                        + " parent_Legion VARCHAR(50),"
                                        + " marines_count BIGINT NOT NULL CHECK(marines_Count > 0),"
                                        + " world VARCHAR(50),"
                                        + " CHECK(marines_count <=1000));";
        Statement stat = connectionDB.createStatement();
        stat.execute(createTableQuery);
    }

    static void createTypeCategory(Connection connectionDB) {
        try {
            final String createTableQuery = "CREATE TYPE astartes_category AS ENUM ("
                                            + " 'aggressor',"
                                            + " 'inceptor',"
                                            + " 'tactical',"
                                            + " 'chaplain',"
                                            + " 'helix');";
            Statement stat = connectionDB.createStatement();
            stat.execute(createTableQuery);
        } catch (SQLException e) {
        }
    }

    static void createSpMarTable(Connection connectionDB) throws SQLException {
        final String createTableQuery = "CREATE TABLE IF NOT EXISTS SpaceMarine ("
                                        + " id SERIAL PRIMARY KEY,"
                                        + " name VARCHAR(50) NOT NULL,"
                                        + " creating_date_time TIMESTAMP NOT NULL,"
                                        + " coordinates BIGINT NOT NULL,"
                                        + " health INT CHECK(health>0),"
                                        + " heart_count INT CHECK(heart_count > 0),"
                                        + " loyal BOOLEAN,"
                                        + " astartes_category astartes_category NOT NULL,"
                                        + " chapter BIGINT,"
                                        + " owner_name VARCHAR(100) not null,"
                                        + " CHECK(heart_count <= 3),"
                                        + " FOREIGN KEY(coordinates) REFERENCES coordinates(id),"
                                        + " FOREIGN KEY(chapter) REFERENCES chapter(id),"
                                        + " FOREIGN KEY(owner_name) REFERENCES users(login));";
        Statement stat = connectionDB.createStatement();
        stat.execute(createTableQuery);
    }
}
