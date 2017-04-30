package org.otrmessenger;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sfrolov on 4/8/17.
 */
public class SQLiteDBHandler {
    protected Connection conn;
    protected String pathToDB;

    SQLiteDBHandler(String _pathToDB) {
        pathToDB = _pathToDB;
        connectToDB();
        createTables();
    }

    protected void connectToDB() {
        // credits: based on http://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
        try {
            // db parameters
            String url = "jdbc:sqlite:" + pathToDB;
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }/* finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }*/
    }

    public void reset() {
        // TODO
        connectToDB();
        createTables();
    }

    protected void createTables() {
        List<String> sqlQueries = new LinkedList<String>();
        sqlQueries.add("CREATE TABLE  USERS ( " +
                "USERNAME           TEXT           PRIMARY KEY, " +
                "PASSHASH           varbinary(32), " +
                "PUBLIC_SIGN_KEY    varbinary(32)," +
                "ADMIN              boolean not null default 0);");
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

    public List<byte[]> getUsers() {
        // loosely based on http://www.sqlitetutorial.net/sqlite-java/select/
        String sql = "SELECT USERNAME FROM USERS";
        List<byte[]> users = new ArrayList<byte[]>();

        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                users.add(rs.getBytes("USERNAME"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public Boolean checkPassword(byte[] name, byte[] passHash) {
        if ((passHash.length == 0) || (name.length == 0)) {
            return false;
        }
        String sql = "SELECT PASSHASH FROM USERS  "
                + "WHERE USERNAME = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            stmt.setBytes(1, name);
            // update
            ResultSet rs  = stmt.executeQuery();
            byte[] dbPassHash = rs.getBytes("PASSHASH");
            return Arrays.equals(dbPassHash, passHash);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Boolean checkAdminPassword(byte[] name, byte[] passHash) {
        if ((passHash.length == 0) || (name.length == 0)) {
            return false;
        }
        String sql = "SELECT PASSHASH FROM USERS  "
                + "WHERE USERNAME = ? AND ADMIN=1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            stmt.setBytes(1, name);
            // update
            ResultSet rs  = stmt.executeQuery();
            byte[] dbPassHash = rs.getBytes("PASSHASH");
            return Arrays.equals(dbPassHash, passHash);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public byte[] getKey(byte[] name) {
        String sql = "SELECT PUBLIC_SIGN_KEY FROM USERS "
                + "WHERE USERNAME = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            stmt.setBytes(1, name);
            // update
            ResultSet rs  = stmt.executeQuery();
            return rs.getBytes("PUBLIC_SIGN_KEY");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Boolean setKey(byte[] name, byte[] passHash) {
        // loosely based on http://www.sqlitetutorial.net/sqlite-java/update/
        String sql = "UPDATE USERS SET PASSHASH = ? "
                + "WHERE USERNAME = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            stmt.setBytes(1, passHash);
            stmt.setBytes(2, name);
            // update
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Boolean addUser(byte[] name, byte[] passHash) {
        // loosely based on http://www.sqlitetutorial.net/sqlite-java/insert/
        String sql = "INSERT INTO USERS (USERNAME, PASSHASH) VALUES(?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, name);
            stmt.setBytes(2, passHash);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Boolean userExists(byte[] name) {
        String sql = "SELECT * from USERS "
                + "WHERE USERNAME = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            stmt.setBytes(1, name);
            // update
            ResultSet rs  = stmt.executeQuery();
            return (rs.getString("USERNAME").length() > 0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
