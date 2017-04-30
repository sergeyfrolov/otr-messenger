package org.otrmessenger.crypto;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class KeyPair {
	private PublicKey pub;
	private PrivateKey priv;
	
	public KeyPair(String type){
        KeyPairGenerator keyGen = null;
        SecureRandom random = null;
	    if (type.equals("signing")){
            try {
                keyGen = KeyPairGenerator.getInstance("DSA");
                random = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
	    }
	    else{
            try {
                keyGen = KeyPairGenerator.getInstance("DiffieHellman");
                random = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
	    }
        keyGen.initialize(1024, random);

        java.security.KeyPair pair = keyGen.generateKeyPair();
        this.priv = pair.getPrivate();
        this.pub = pair.getPublic();

	}
	
	public KeyPair(PublicKey p){
	    this.pub = p;
	}

    public PublicKey getPub() {
        return pub;
    }

    public PrivateKey getPriv() {
        return priv;
    }
}
