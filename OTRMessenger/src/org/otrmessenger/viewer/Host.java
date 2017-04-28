package org.otrmessenger.viewer;
import java.util.ArrayList;

import org.otrmessenger.crypto.KeyPair;
import org.otrmessenger.viewer.ServerConnector;
import org.otrmessenger.viewer.FriendsList;
import org.otrmessenger.viewer.Chat;
import org.otrmessenger.Message;

public class Host extends User {
	private KeyPair DHKeys;
	private ServerConnector SC;
	private boolean isAdmin;
	private FriendsList fl; 
	private ArrayList<Chat> chats;
	
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
