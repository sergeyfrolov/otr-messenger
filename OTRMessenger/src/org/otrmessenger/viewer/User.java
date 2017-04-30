package org.otrmessenger.viewer;

import org.otrmessenger.crypto.Signer;
import org.otrmessenger.messaging.Messaging.Message;
public class User {
	protected String username;
	protected Signer signature;
	
	public User(){
	    this("");
	}

	public User(String u){
	    this.username = u;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Signer getSignature() {
		return signature;
	}

	public void setSignature(Signer signature) {
		this.signature = signature;
	}
	
	public boolean verifyMessage(Message m){
	    if (!this.signature.equals(null))
            return this.signature.verify(m);
	    
	    return false;
	}
	
	public String toString(){
	    return this.username;
	}
}
