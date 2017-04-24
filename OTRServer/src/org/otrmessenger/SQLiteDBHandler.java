package org.otrmessenger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sfrolov on 4/8/17.
 */
public class SQLiteDBHandler {
    private Connection conn;

    SQLiteDBHandler(String pathToDB) {
        // credits: based on http://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
        try {
            // db parameters
            String url = "jdbc:sqlite:" + pathToDB;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            createTables();

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void createTables() {
        List<String> sqlQueries = new LinkedList<String>();
        sqlQueries.add("CREATE TABLE IF NOT EXISTS USERS " +
                " USERNAME       TEXT           PRIMARY KEY    NOT NULL, " +
                " PASSHASH       varbinary(32), " +
                " KEY            varbinary(32)) ");
        for (String sqlQuery: sqlQueries) {
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sqlQuery);
                stmt.close();
            } catch (Exception e) {
                System.err.println("Failed to execute \"" + sqlQuery + "\": " + e.getMessage());
            }
        }
        System.out.println("All tables created successfully");
    }

    ArrayList<String> getUsers() {
        return null; // TODO
    }

    Boolean checkPassword(byte[] name, byte[] passHash) {
        return null; // TODO
    }

    Boolean checkAdminPassword(byte[] name, byte[] passHash) {
        return null; // TODO
    }

    byte[] getKey(byte[] name) {
        return null; // TODO
    }

    Boolean setKey(byte[] name, byte[] key) {
        return null; // TODO
    }

    Boolean addUser(byte[] name, byte[] passHash) {
        return null; // TODO
    }

    Boolean userExists(String username) {
        return null; // TODO
    }
}
