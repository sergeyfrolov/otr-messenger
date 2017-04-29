package org.otrmessenger.viewer;
import java.util.ArrayList;

import org.otrmessenger.crypto.KeyPair;
import org.otrmessenger.ServerConnector;
import org.otrmessenger.viewer.FriendsList;
import org.otrmessenger.viewer.Chat;
import org.otrmessenger.Message;

public class Host extends User {
	private KeyPair DHKeys;
	private ServerConnector SC;
	private boolean isAdmin;
	private FriendsList fl; 
	private ArrayList<Chat> chats;
	
	public Host() {
	    this.SC = new ServerConnector();
	    this.fl = new FriendsList();
	}
	
	public Host(String s, String password){
	    this.username = s;
	    this.SC = new ServerConnector(s, password.getBytes(), "10.233.19.23", 10050);
//	    this.SC = new ServerConnector(s, password.getBytes(), "localhost", 10050);
	    this.fl = new FriendsList(this.username);
	}
	
	public boolean addFriend(String n, String g){
	    boolean ret = this.SC.addFriend(n);
	    if (ret){
	        Group gr = new Group();
	        gr.setName(g);
            this.fl.addFriendToGroup(new User(n), gr);
            this.fl.save();
	    }
	    
	    return ret;
	}
	
	public boolean login(){
	    return this.SC.loginUser();
	}
	
	public boolean signUp() {
	    return this.SC.signUp();
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
		boolean confirm = false;
		//TODO
		return confirm;
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
