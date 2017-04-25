package org.otrmessenger;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfrolov on 4/8/17.
 */
public class AssetHandler {
    private SQLiteDBHandler dbHandler;
    private CertFileHandler certHandler;

    AssetHandler() {
        dbHandler = new SQLiteDBHandler("users.db");
        certHandler = new CertFileHandler("key.pem");
    }

    List<byte[]> getUsers() {
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

    void addUser(byte[] name, byte[] passHash){dbHandler.addUser(name, passHash); }

    X509Certificate getCertKey() {
        return certHandler.getCertKey();
    }

    Boolean userExists(byte[] username) {
        return dbHandler.userExists(username);
    }
}
