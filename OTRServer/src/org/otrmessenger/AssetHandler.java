package org.otrmessenger;

import java.util.ArrayList;

/**
 * Created by sfrolov on 4/8/17.
 */
public class AssetHandler {
    private SQLiteDBHandler dbHandler;
    private CertFileHandler certHandler;

    ArrayList<String> getUsers() {
        return dbHandler.getUsers();
    }

    Boolean checkPassword(byte[] name, byte[] passHash) {
        return dbHandler.checkPassword(name, passHash);
    }

    Boolean checkAdminPassword(byte[] name, byte[] passHash) {
        return dbHandler.checkAdminPassword(name, passHash);
    }

    byte[] getKey(byte[] name) {
        return dbHandler.getKey(name);
    }

    Boolean setKey(byte[] name, byte[] key) {
        return dbHandler.setKey(name, key);
    }

    Boolean addUser(byte[] name, byte[] passHash) {
        return dbHandler.addUser(name, passHash);
    }

    byte[] getCertKey() {
        return certHandler.getCertKey();
    }

    Boolean userExists(String username) {
        return dbHandler.userExists(username);
    }
}
