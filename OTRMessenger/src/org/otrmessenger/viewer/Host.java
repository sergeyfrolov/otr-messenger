package org.otrmessenger.viewer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import org.otrmessenger.crypto.KeyPair;
import org.otrmessenger.crypto.Signer;
import org.otrmessenger.messaging.Messaging.Message;
import org.otrmessenger.ServerConnector;
import org.otrmessenger.viewer.FriendsList;
import org.otrmessenger.viewer.Chat;


public class Host extends User {
	private KeyPair DHKeys;
	private ServerConnector SC;
	private boolean isAdmin;
	private FriendsList fl; 
	private ArrayList<Chat> chats;
	private Thread thread;
	
//	public Host() {
//	    this.SC = new ServerConnector();
//	    this.fl = new FriendsList();
//	}
	
	public Host(String s, String password){
	    this.DHKeys = new KeyPair("encryption");
	    setSignature(new Signer());
	    this.chats = new ArrayList<Chat> ();
	    this.username = s;
//	    this.SC = new ServerConnector(s, password.getBytes(), "10.233.19.23", 10050);
//	    this.SC = new ServerConnector(s, password.getBytes(), "localhost", 10050);
	    this.SC = new ServerConnector(this, password.getBytes(), "10.233.19.23", 10050);
//	    this.SC = new ServerConnector(this, password.getBytes(), "localhost", 10050);
	    
//	    this.thread = new Thread(this.SC);
//	    this.thread.start();
	    this.fl = new FriendsList(this.username);
	}
	
	public void setEncryptionKey(){
	    this.SC.setEncryptionKey(this.DHKeys.getPublic().getEncoded());
	}
	
	public PublicKey getPublicKey(){
	    return this.DHKeys.getPublic();
	}
	
	public KeyPair getKeyPair(){
	    return this.DHKeys;
	}
	
	public PublicKey requestEncryptionKey(User other){
	    PublicKey ret = null;
        try {
            this.SC.terminate();
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            ret = KeyFactory.getInstance("DiffieHellman").generatePublic(
                    new X509EncodedKeySpec(this.SC.requestEncryptionKey(other.getUsername())));
            this.SC.restart();
            this.thread = new Thread(this.SC);
            this.thread.start();
            return ret;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.SC.restart();
            this.thread = new Thread(this.SC);
            this.thread.start();
            return ret;
        }
	    
	}
	
	public void addSigningKeyToServer(){
//        try {
//            this.SC.terminate();
//            this.thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
	    this.SC.sendSigningKey(this.getSignature().getPublicKey());
//	    this.SC.restart();
//	    this.thread = new Thread(this.SC);
//	    this.thread.start();
	}
	
	public Message signMessage(Message txt){
	    return this.signature.sign(txt);
	}
	
	public boolean addFriend(String n, String g){
        try {
            this.SC.terminate();
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    boolean ret = this.SC.addFriend(n);
	    this.SC.restart();
	    this.thread = new Thread(this.SC);
	    this.thread.start();
	    if (ret){
	        Group gr = new Group();
	        gr.setName(g);
            this.fl.addFriendToGroup(new User(n), gr);
            this.fl.save();
	    }
	    
	    return ret;
	}
	
	public boolean requestSigningKey(User other){
        try {
            this.SC.terminate();
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PublicKey publicKey;
        try {
            publicKey = KeyFactory.getInstance("DSA").generatePublic(
                    new X509EncodedKeySpec(this.SC.requestSigningKey(other.getUsername())));
            other.setSignature(new Signer(new KeyPair(publicKey)));
            this.SC.restart();
            this.thread = new Thread(this.SC);
            this.thread.start();
            return true;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.SC.restart();
            this.thread = new Thread(this.SC);
            this.thread.start();
            return false;
        }
	}
	
	public boolean login(){
//        this.SC.terminate();
//        try {
//            this.thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        boolean ret = this.SC.loginUser();
	    addSigningKeyToServer();
	    setEncryptionKey();
	    if (ret){
            this.SC.restart();
            this.thread = new Thread(this.SC);
            this.thread.start();
	    }
	    return ret;
	}
	
	public boolean signUp() {
//        try {
//            this.SC.terminate();
//            this.thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        boolean ret = this.SC.signUp();
	    this.SC.restart();
	    this.thread = new Thread(this.SC);
	    this.thread.start();
	    return ret;
	}
	
	public void drawFriendsList(){
	    
	}

	public boolean genKeyPair(){
		boolean confirm = false;
		//TODO
		return confirm;
	}
	private boolean displayGUI(){
		boolean confirm = false;
		//TODO
		return confirm;
	}
	public boolean sendMessage(User to, Message msg){
        this.SC.terminate();
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean ret = this.SC.sendMessage(to, msg);
	    this.SC.restart();
	    this.thread = new Thread(this.SC);
	    this.thread.start();
		return ret;
	}

	public boolean receiveMessage(Message msg){
		for (Chat c: this.chats){
		    if (msg.getFromUsername().toStringUtf8().equals(c.getOther().getUsername())){
		        c.receiveMessage(msg);
		        return true;
		    }
		}
		
		// here don't already have a chat with user, need to open one.
		
		Chat c = new Chat(msg.getFromUsername().toStringUtf8(), this);
		
		addChat(c);
		c.receiveMessage(msg);

		return true;
	}
	public boolean addChat(Chat c){
		return chats.add(c);
	}
}
