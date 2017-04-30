package org.otrmessenger;

import org.otrmessenger.messaging.Messaging.Message;
import java.util.ArrayList;

public class History {
	private ArrayList<Message> savedMsgs;
	
	public History() {
	    this.savedMsgs = new ArrayList<Message>();
	}

	public void addMsg(Message text){
		savedMsgs.add(text);
	}
	public Message getMsg(int index){
		
		return savedMsgs.get(index); 
	}
	
	public int numMessages(){
	    return savedMsgs.size();
	}
}
