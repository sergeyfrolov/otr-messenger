package org.otrmessenger.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import org.otrmessenger.messaging.Messaging.*;

import com.google.protobuf.ByteString;

public class Signer {
    private KeyPair kp;
    private Signature sig;
    
    public Signer(){
        this.kp = new KeyPair("signing");
        try {
            this.sig = Signature.getInstance("SHA1withDSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    public Signer(KeyPair kp){
        this.kp = kp;
        try {
            this.sig = Signature.getInstance("SHA1withDSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    public Message sign(Message txt){
        Message.Builder m = Message.newBuilder();
        try {
            this.sig.initSign(this.kp.getPrivate());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        
//        System.out.println("Pubkey in signer: " + txt.getPubkey().toStringUtf8());
        m.setPubkey(txt.getPubkey());
        m.setText(txt.getText());
        m.setIv(txt.getIv());
        m.setFromUsername(txt.getFromUsername());
        m.setToUsername(txt.getToUsername());
        try {
            this.sig.update(txt.getText().toByteArray());
            m.setSignature(ByteString.copyFrom(this.sig.sign()));
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        return m.build();
    }
    
    public boolean verify(Message txt){
        try {
            this.sig.initVerify(this.kp.getPublic());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        
        try {
            this.sig.update(txt.getText().toByteArray());
            return this.sig.verify(txt.getSignature().toByteArray());
        } catch (SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public byte[] getPublicKey(){
        return this.kp.getPublic().getEncoded();
    }

}
