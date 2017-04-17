package org.otrmessenger;
import org.otrmessenger.Message;
import java.util.ArrayList;

public class History {
	private ArrayList<Message> savedMsgs;
	
	public void addMsg(Message text){
		savedMsgs.add(text);
		
	}
	public Message getMsg(int index){
		
		return savedMsgs.get(index); 
	}

}
