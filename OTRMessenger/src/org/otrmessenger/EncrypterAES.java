package org.otrmessenger;

import java.security.SecureRandom;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.GCMParameterSpec;

public class EncrypterAES {
	private SecretKey key;
	private Cipher cipher;
	private final int tagLen = 128;
	private IvParameterSpec IV;
	
	public EncrypterAES(Key key) throws NoSuchAlgorithmException{
		this.key = key.getSecret();
		try{
			cipher = Cipher.getInstance("AES/GCM/PKCS5PADDING");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		updateIV();
	}
	
	public void setIV(String iv){
		this.IV = new IvParameterSpec(iv.getBytes());
	}

	public Message encrypt(Message txt) throws InvalidKeyException, 
	InvalidAlgorithmParameterException, IllegalBlockSizeException, 
	BadPaddingException {
		Message m = new Message();
		cipher.init(Cipher.ENCRYPT_MODE, this.key, 
				new GCMParameterSpec(this.tagLen, this.IV.getIV()));
		cipher.update(txt.getText());
		m.setText(cipher.doFinal());
		updateIV();

		return m;
	}
	
	public Message decrypt(Message txt) throws InvalidKeyException, 
	InvalidAlgorithmParameterException, IllegalBlockSizeException, 
	BadPaddingException{
		Message m = new Message();
		cipher.init(Cipher.DECRYPT_MODE, this.key, 
				new GCMParameterSpec(this.tagLen, this.IV.getIV()));
		cipher.update(txt.getText());
		m.setText(cipher.doFinal());
		
		return m;
	}
	
	private void updateIV(){
		byte[] iv = new byte[cipher.getBlockSize()];
		try {
			SecureRandom.getInstance("SHA1PRNG").nextBytes(iv);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		this.IV = new IvParameterSpec(iv);
	}

	public SecretKey getKey() {
		return key;
	}

	public void setKey(SecretKeySpec key) {
		this.key = key;
	}

	public byte[] getIV() {
		return IV.getIV();
	}
}