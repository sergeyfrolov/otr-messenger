package org.otrmessenger;

import java.io.*;
import java.net.Socket;

import org.otrmessenger.messaging.Messaging.Message;
import org.otrmessenger.viewer.User;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.otrmessenger.messaging.Messaging.*;

public class ServerConnector implements Runnable {
    private Socket sock;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private Credentials cred;
    private boolean running;
    
    public ServerConnector(String usrName, byte[] passHash, String address, int port){
        cred = credSetup(ByteString.copyFromUtf8(usrName), 
                ByteString.copyFrom(passHash), false, false);
        try {
            sock = new Socket(address, port);
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());
            running = true;
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
    
    public void run(){
        System.out.println("in run");
        while(running){
            int length = 0;
            byte[] buf;
            try {
                length = in.readInt();
                System.out.println(length);
                buf = new byte[length];
                in.readFully(buf);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            MsgServerToClient msg = null;
            try {
                msg = MsgServerToClient.parseFrom(buf);
                System.out.println(msg);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                return;
            }
        }
    }
    
    public boolean loginUser(){
        System.out.println("in loginUser");
        return initialConnection(false);
    }
    
    private Credentials credSetup(ByteString username, ByteString passwordHash,
            boolean signUp, boolean admin){
        Credentials.Builder credBuilder = Credentials.newBuilder();
        credBuilder.setUsername(username);
        credBuilder.setPasswordHash(passwordHash);
        credBuilder.setSignUp(signUp);
        credBuilder.setAdmin(false);
        return credBuilder.build();
    }

    private boolean initialConnection(boolean signUp){
        System.out.println("in initialConnection");
        cred = credSetup(cred.getUsername(), cred.getPasswordHash(), signUp, false);
        MsgClientToServer.Builder ctsBuilder = MsgClientToServer.newBuilder();
        ctsBuilder.setCredentials(cred);
        MsgServerToClient msg = send(ctsBuilder.build());
        System.out.println(msg);
        
        return msg.getLoginSuccess();
    }

    public boolean signUp(){
        return initialConnection(true);
    }

    public boolean sendMessage(User to, Message msg){
//        cred = credSetup(cred.getUsername(), cred.getPasswordHash(), false, false);

        Message newMsg = msgSetup(cred.getUsername(), ByteString.copyFromUtf8(to.getUsername()),
                msg.getIv(), msg.getSignature(), msg.getText());

        MsgClientToServer.Builder ctsBuilder = MsgClientToServer.newBuilder();
//        ctsBuilder.setCredentials(cred);
        ctsBuilder.setMsg(newMsg);
        MsgServerToClient response = send(ctsBuilder.build());
//        System.out.println(response);
        return response.getMsgStatus().getStatus() == MessageStatus.DELIVERED;
    }

    private Message msgSetup(ByteString fromUser, ByteString toUser,
            ByteString iv, ByteString signature, ByteString text) {
        Message.Builder msgBuilder = Message.newBuilder();
        msgBuilder.setFromUsername(fromUser);
        msgBuilder.setToUsername(toUser);
        msgBuilder.setIv(iv);
        msgBuilder.setSignature(signature);
        msgBuilder.setText(text);
        return msgBuilder.build();
    }

    public MsgServerToClient send(MsgClientToServer cts){
        System.out.println("in send");
        try{
            out.writeInt(cts.getSerializedSize());
            out.flush();
            out.write(cts.toByteArray());
            out.flush();
            System.out.println("after outs");
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
        int length = 0;
        byte[] buf;
        try {
            length = in.readInt();
            buf = new byte[length];
            in.readFully(buf);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        MsgServerToClient msg = null;
        try {
            msg = MsgServerToClient.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }

        return msg;
    }
    
    public void close(){
        try {
            sock.close();
            running = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void terminate(){
        running = false;
    }
}
