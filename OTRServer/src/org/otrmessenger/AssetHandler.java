package org.otrmessenger;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfrolov on 4/8/17.
 */
public class AssetHandler {
    protected SQLiteDBHandler dbHandler;
    protected CertFileHandler certHandler;

    AssetHandler() {
        dbHandler = new SQLiteDBHandler("users.db");
        certHandler = new CertFileHandler("key.pem");
    }

    public List<byte[]> getUsers() {
        return dbHandler.getUsers();
    }

    public void reset() {
        dbHandler.reset();
    }

    public Boolean checkPassword(byte[] name, byte[] passHash) {
        return dbHandler.checkPassword(name, passHash);
    }

    public Boolean checkAdminPassword(byte[] name, byte[] passHash) {
        return dbHandler.checkAdminPassword(name, passHash);
    }

    public byte[] getKey(byte[] name) {
        return dbHandler.getKey(name);
    }

    public Boolean setKey(byte[] name, byte[] key) {
        return dbHandler.setKey(name, key);
    }

    public Boolean addUser(byte[] name, byte[] passHash){return dbHandler.addUser(name, passHash); }

    public X509Certificate getCertKey() {
        return certHandler.getCertKey();
    }

    public Boolean userExists(byte[] username) {
        return dbHandler.userExists(username);
    }
}
