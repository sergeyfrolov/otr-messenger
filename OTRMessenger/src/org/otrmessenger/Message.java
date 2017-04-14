package org.otrmessenger;

import java.util.Base64;

public class Message {
	private byte[] text;
	private byte[] tag;
	private byte[] IV;
	
	public Message(){
		this.text = "".getBytes();
		this.tag = new byte[46];
	}

	public Message(String s){
		text = s.getBytes();
		this.tag = new byte[46];
	}

	public byte[] getText() {
		return text;
	}

	public void setText(String text){
		this.text = text.getBytes();
	}

	public void setText(byte[] text) {
		this.text = text;
	}

	public byte[] getTag() {
		return tag;
	}

	public String printTag() {
	    try{
            return Base64.getEncoder().encodeToString(this.tag);
	    }
	    catch(NullPointerException e){
	        return "null";
	    }
	}

	public void setTag(byte[] tag) {
		this.tag = tag;
		System.out.println("size of tag = " + this.tag.length);
	}

	public byte[] getIV() {
		return IV;
	}

	public void setIV(byte[] IV) {
		this.IV = IV;
	}
	
	public String toString(){
		return Base64.getEncoder().encodeToString(this.text);
	}
}
