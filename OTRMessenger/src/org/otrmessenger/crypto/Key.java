package org.otrmessenger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Key {
	private byte[] key;
	private SecretKey secret;
	private final int ITCOUNT = 65536;
	private final int AESKEYLEN = 128;
	
	public Key(String pass, String salt, String type) 
			throws NoSuchAlgorithmException, InvalidKeySpecException{
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		if (type.equals("AES")){
			KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt.getBytes(), this.ITCOUNT, this.AESKEYLEN);
			SecretKey tmp = factory.generateSecret(spec);
			secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		}
		key = pass.getBytes();
	}
	
	public byte[] getKey(){return key;}
	
	public SecretKey getSecret() {
		return secret;
	}

	public String toString(){
		return Base64.getEncoder().encodeToString(key);
	}
}
