package org.otrmessenger.crypto;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class KeyPair {
	private PublicKey pub;
	private PrivateKey priv;
	
	public KeyPair(){
	    KeyPairGenerator keyGen = null;
        SecureRandom random = null;
        try {
            keyGen = KeyPairGenerator.getInstance("DSA");
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        keyGen.initialize(1024, random);

        java.security.KeyPair pair = keyGen.generateKeyPair();
        this.priv = pair.getPrivate();
        this.pub = pair.getPublic();

	}

    public PublicKey getPub() {
        return pub;
    }

    public PrivateKey getPriv() {
        return priv;
    }
}
