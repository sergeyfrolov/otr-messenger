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

    public byte[] getSignKey(byte[] name) {
        return dbHandler.getSignKey(name);
    }

    public Boolean setSignKey(byte[] name, byte[] key) {
        return dbHandler.setSignKey(name, key);
    }

    public byte[] getEncryptionKey(byte[] name) {
        return dbHandler.getEncryptionKey(name);
    }

    public Boolean setEncryptionKey(byte[] name, byte[] key) {
        return dbHandler.setEncryptionKey(name, key);
    }

    public Boolean addUser(byte[] name, byte[] passHash){return dbHandler.addUser(name, passHash); }

    public X509Certificate getCertKey() {
        return certHandler.getCertKey();
    }

    public Boolean userExists(byte[] username) {
        return dbHandler.userExists(username);
    }
}
