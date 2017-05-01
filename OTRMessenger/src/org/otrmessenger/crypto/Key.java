package org.otrmessenger.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Key {
	private byte[] key;
	private SecretKey secret;
	private final int ITCOUNT = 65536;
	private final int AESKEYLEN = 128;
	
	public Key(){
	    KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
	    keyGen.init(this.AESKEYLEN);
	    this.secret = keyGen.generateKey();
	}

	public Key(String pass, String salt, String type){
			SecretKeyFactory factory = null;
            try {
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            }

		if (type.equals("AES")){
			KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt.getBytes(), this.ITCOUNT, this.AESKEYLEN);
			SecretKey tmp = null;
            try {
                tmp = factory.generateSecret(spec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
			this.secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		}
		this.key = pass.getBytes();
	}
	
	public byte[] getKey(){return this.key;}
	
	public SecretKey getSecret() {
		return this.secret;
	}

	public String toString(){
		return Base64.getEncoder().encodeToString(this.key);
	}
}
