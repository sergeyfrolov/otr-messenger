package org.otrmessenger.crypto;

import java.security.SecureRandom;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.otrmessenger.Message;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.GCMParameterSpec;

public class EncrypterAES {
	private SecretKey key;
	private Cipher cipher;
	private final int tagLen = 128;
	private IvParameterSpec IV;
	
	public EncrypterAES(Key key){
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

	public Message encrypt(Message txt){
		Message m = new Message();
		try {
            cipher.init(Cipher.ENCRYPT_MODE, this.key, 
            		new GCMParameterSpec(this.tagLen, this.IV.getIV()));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
		cipher.update(txt.getText());
		try {
            m.setText(cipher.doFinal());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
		m.setIV(this.IV.getIV());
		updateIV();

		return m;
	}
	
	public Message decrypt(Message txt){
		this.IV = new IvParameterSpec(txt.getIV());
		Message m = new Message();
		try {
            cipher.init(Cipher.DECRYPT_MODE, this.key, 
            		new GCMParameterSpec(this.tagLen, this.IV.getIV()));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		cipher.update(txt.getText());
		try {
            m.setText(cipher.doFinal());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
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