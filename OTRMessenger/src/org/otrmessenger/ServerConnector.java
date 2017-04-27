package org.otrmessenger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import org.otrmessenger.messaging.Messaging.Message;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.otrmessenger.messaging.Messaging.*;

public class ServerConnector {
    private Socket sock;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private Credentials cred;
    
    public ServerConnector(String usrName, byte[] passHash, String address, int port){
        
        Credentials.Builder credBuilder = Credentials.newBuilder();
        credBuilder.setUsername(ByteString.copyFromUtf8(usrName));
        credBuilder.setPasswordHash(ByteString.copyFrom(passHash));
        credBuilder.setAdmin(false);
        credBuilder.setSignUp(false);
        cred = credBuilder.build();
        try {
            sock = new Socket(address, port);
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerConnector(String address, int port){
        this("","".getBytes(), address, port);
    }
    
    public ServerConnector(int port){
        this("localhost", port);
    }
    
    public ServerConnector(){
        this("localhost", 10050);
    }
    
    public boolean loginUser(){
        return initialConnection(false);
    }
    
    private boolean initialConnection(boolean signUp){
        Credentials.Builder credBuilder = Credentials.newBuilder();
        credBuilder.setUsername(cred.getUsername());
        credBuilder.setPasswordHash(cred.getPasswordHash());
        credBuilder.setSignUp(signUp);
        credBuilder.setAdmin(false);
        cred = credBuilder.build();
        MsgClientToServer.Builder ctsBuilder = MsgClientToServer.newBuilder();
        ctsBuilder.setCredentials(cred);
        MsgClientToServer cts = ctsBuilder.build();
        
        try{
            out.writeInt(cts.getSerializedSize());
            out.write(cts.toByteArray());
        }
        catch(IOException e){
            e.printStackTrace();
            return false;
        }
        int length = 0;
        byte[] buf;
        try {
            length = in.readInt();
            buf = new byte[length];
            in.readFully(buf);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        MsgServerToClient msg = null;
        try {
            msg = MsgServerToClient.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return false;
        }
        
        return msg.getLoginSuccess();
    }

    public boolean signUp(){
        return initialConnection(true);
    }

    public void send(String s){
        MsgClientToServer.Builder msg = MsgClientToServer.newBuilder();
		Message.Builder msgBuilder = Message.newBuilder();
		msgBuilder.setText(ByteString.copyFromUtf8(s));
		msgBuilder.setFromUsername(ByteString.copyFromUtf8("Ian"));
		msg.setMsg(msgBuilder.build());
		Credentials.Builder credBuilder = Credentials.newBuilder();
		credBuilder.setSignUp(true);
		credBuilder.setUsername(ByteString.copyFromUtf8("Ian"));
		credBuilder.setPasswordHash(ByteString.copyFrom("secret".getBytes()));
		credBuilder.setAdmin(false);
		cred = credBuilder.build();
		msg.setCredentials(cred);
		MsgClientToServer m = msg.build();
        try {
            out.writeInt(m.getSerializedSize());
            out.write(m.toByteArray());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
