package org.otrmessenger.viewer;
import java.util.ArrayList;

import org.otrmessenger.crypto.KeyPair;
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
	    this.username = s;
	    this.SC = new ServerConnector(s, password.getBytes(), "10.233.19.23", 10050);
//	    this.SC = new ServerConnector(s, password.getBytes(), "localhost", 10050);
	    
//	    this.thread = new Thread(this.SC);
//	    this.thread.start();
	    this.fl = new FriendsList(this.username);
	}
	
	public boolean addFriend(String n, String g){
        try {
            this.SC.terminate();
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    boolean ret = this.SC.addFriend(n);
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
	
	public boolean login(){
//        this.SC.terminate();
//        try {
//            this.thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        boolean ret = this.SC.loginUser();
	    this.thread = new Thread(this.SC);
	    this.thread.start();
	    return ret;
	}
	
	public boolean signUp() {
        try {
            this.SC.terminate();
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean ret = this.SC.signUp();
	    this.thread = new Thread(this.SC);
	    this.thread.start();
	    return ret;
	}
	
	public void drawFriendsList(){
	    
	}

	private boolean genKeyPair(){
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
	    this.thread = new Thread(this.SC);
	    this.thread.start();
		return ret;
	}
	public boolean receiveMessage(User from, Message msg){
		boolean confirm = false;
		//TODO
		return confirm;
	}
	public boolean addChat(User other){
		boolean confirm = false;
		//TODO
		return confirm;
	}
}
