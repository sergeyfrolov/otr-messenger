package org.otrmessenger;

import java.util.Base64;

public class Message {
	private byte[] text;
	private byte[] tag;
	private byte[] IV;
	
	public Message(){
		this.text = "".getBytes();
	}

	public Message(String s){
		text = s.getBytes();
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

	public void setTag(byte[] tag) {
		this.tag = tag;
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
