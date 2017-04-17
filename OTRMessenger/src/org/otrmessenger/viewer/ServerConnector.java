package org.otrmessenger.viewer;
import javax.net.ssl.SSLSocket;
import org.otrmessenger.messaging.Messaging.Credentials;
import org.otrmessenger.crypto.KeyPair;
public class ServerConnector {
	private SSLSocket sock;
	private Credentials curr;
	private byte[] passHash;
	
	public boolean connectToServer(){
		boolean confirm = false;
		//TODO
		return confirm;
	}
	public boolean loginUser(){
		boolean confirm = false;
		//TODO
		return confirm;
	}
	public boolean signUp(){
		boolean confirm = false;
		//TODO
		return confirm;
	}
	public boolean updateKey(KeyPair kp){
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
}
