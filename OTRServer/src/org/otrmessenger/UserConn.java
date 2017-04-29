package org.otrmessenger;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.otrmessenger.messaging.Messaging;
import org.otrmessenger.messaging.Messaging.Credentials;
import org.otrmessenger.messaging.Messaging.MsgClientToServer;
import org.otrmessenger.messaging.Messaging.MsgServerToClient;
import org.otrmessenger.messaging.Messaging.AdminRequest;
import org.otrmessenger.messaging.Messaging.ClientInfo;
import org.otrmessenger.messaging.Messaging.Message;

/**
 * Created by sfrolov on 4/8/17.
 */
public class UserConn implements Runnable {
    protected String username;
    //protected SSLSocket sock;
    protected Socket sock;
    protected Boolean admin;
    protected Boolean loggedIn;

    protected AssetHandler assets;
    protected OTRServer server;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    UserConn(Socket sock) {
        this.sock = sock;
        this.username = "";
        this.admin = false;
        this.loggedIn = false;
    }

    @Override
    public void run() {
        server = OTRServer.getInstance();
        assets = server.getAssets();

        try {
            inputStream = new DataInputStream(this.sock.getInputStream());
            outputStream = new DataOutputStream(this.sock.getOutputStream());
        } catch (IOException e) {
            //report exception somewhere.
            printPretty(e.toString()  + ":" + e.getMessage());
        }
            while (!sock.isClosed()) {
                MsgClientToServer clientMsg = recvClientMsg();
                if (clientMsg == null) {
                    // If errored during message recv(could be just end of connection)
                    try {
                        sock.close();
                    } catch (IOException e) {
                        printPretty(e.toString()  + ":" + e.getMessage());
                    }
                    return;
                }

                printPretty("received message {" + clientMsg.toString() + "}");

                if (clientMsg.hasCredentials()) {
                    Credentials creds = clientMsg.getCredentials();
                    if (creds.getSignUp()) {
                        HandleSignUp(creds);
                        continue;
                    } else {
                        HandleLogin(creds);
                    }
                }

                if (!loggedIn) {
                    MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
                    msg.setLoginSuccess(false);
                    sendServerMsg(msg.build());
                    continue;
                }

                if (clientMsg.hasAdminReq()) {
                    AdminRequest request = clientMsg.getAdminReq();
                    HandleAdminRequest(request);
                }

                if (clientMsg.hasMsg()) {
                    Message msg = clientMsg.getMsg();
                    HandleSend(msg);
                }

                if (clientMsg.hasRequestInfoUsername()) {
                    ByteString bstrUsername = clientMsg.getRequestInfoUsername();
                    HandleGetUserInfo(bstrUsername.toByteArray());
                }

                if (clientMsg.hasUpdatedKey()) {
                    ByteString bstrKey = clientMsg.getUpdatedKey();
                    HandleUpdateKey(getUsername().getBytes(), bstrKey.toByteArray());
                }

                if (clientMsg.hasRequestKeyPairChange()) {
                    Boolean wat = clientMsg.getRequestKeyPairChange();
                    // TODO: wat?
                }
            }

    }

    String getUsername() {
        return this.username;
    }

    void setUsername(String name) {
        this.username = name;
    }

    //SSLSocket getSocket() {
    Socket getSocket() {
        return this.sock;
    }

    void setSocket(SSLSocket sock) {
        this.sock = sock;
    }

    void setSocket(Socket sock) {
        this.sock = sock;
    }

    Boolean isAdmin() {
        return this.admin;
    }

    private void HandleLogin(Credentials creds) {
        boolean cred_admin = creds.getAdmin();
        boolean success;
        if (cred_admin) {
            success = assets.checkAdminPassword(creds.getUsername().toByteArray(),
                    creds.getPasswordHash().toByteArray());
        } else {
            success = assets.checkPassword(creds.getUsername().toByteArray(),
                    creds.getPasswordHash().toByteArray());
        }
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        msg.setLoginSuccess(success);
        if (success) {
            setUsername(creds.getUsername().toStringUtf8());
            admin = cred_admin;
            loggedIn = true;
        } else {
            // not necessary, but better safe than sorry
            setUsername("");
            admin = false;
            loggedIn = false;
        }
        sendServerMsg(msg.build());
    }

    private void HandleSignUp(Credentials creds) {
        boolean cred_admin = creds.getAdmin();
        boolean success;
        if (cred_admin) {
            // No admin signing up
            success = false;
        } else {
            success = assets.addUser(creds.getUsername().toByteArray(),
                    creds.getPasswordHash().toByteArray());
        }
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        msg.setLoginSuccess(success);
        sendServerMsg(msg.build());
    }

    private void HandleAdminRequest(AdminRequest request) {
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        switch (request) {
            case GET_ALL_KEYS:
                for (byte[] username : assets.getUsers()) {
                    ClientInfo.Builder clientInfo = ClientInfo.newBuilder();
                    clientInfo.setUsername(ByteString.copyFrom(username));
                    clientInfo.setKey(ByteString.copyFrom(assets.getKey(username)));
                    msg.addUsers(clientInfo.build());
                }
            case GET_ALL_USERS:
                for (byte[] username : assets.getUsers()) {
                    ClientInfo.Builder clientInfo = ClientInfo.newBuilder();
                    clientInfo.setUsername(ByteString.copyFrom(username));
                    msg.addUsers(clientInfo.build());
                }
            case GET_ONLINE_USERS:
                for (UserConn userConn : server.getActiveConnections()) {
                    ClientInfo.Builder clientInfo = ClientInfo.newBuilder();
                    clientInfo.setUsername(ByteString.copyFrom(userConn.getUsername().getBytes()));
                    msg.addUsers(clientInfo.build());
                }
            case GET_CURRENT_STATE:
                msg.setState(server.getState());
            case LAUNCH:
                server.Launch();
                msg.setState(server.getState());
            case RESET:
                server.Reset();
                msg.setState(server.getState());
            case STOP:
                server.Stop();
                msg.setState(server.getState());
        }
        sendServerMsg(msg.build());
    }

    private void HandleSend(Message msgFromUser) {
        if (!Arrays.equals(msgFromUser.getFromUsername().toByteArray(), getUsername().getBytes())) {
            printPretty("User " + getUsername() + " tried to spoof message from " +
                    msgFromUser.getFromUsername().toStringUtf8());
            // if we want to figure this out client-side, we can uncomment following return
            return;
        }
        Messaging.MessageStatus status = Messaging.MessageStatus.USER_OFFLINE;
        Messaging.MessageStatusMsg.Builder ackStatus = Messaging.MessageStatusMsg.newBuilder();
        if (msgFromUser.hasId()) {
            ackStatus.setId(msgFromUser.getId());
        }
        for (UserConn userConn : server.getActiveConnections()) {
            if (userConn.getUsername().equals(msgFromUser.getToUsername().toStringUtf8())) {
                status = Messaging.MessageStatus.DELIVERED;
                Message.Builder message = Message.newBuilder();
                message.setToUsername(ByteString.copyFrom(username.getBytes()));
                message.setFromUsername(ByteString.copyFrom(msgFromUser.getToUsername().toStringUtf8().getBytes()));
                message.setText(msgFromUser.getText());
                message.setSignature(msgFromUser.getSignature());

                MsgServerToClient.Builder msgFromServer = MsgServerToClient.newBuilder();
                msgFromServer.setMsg(message);

                userConn.sendServerMsg(msgFromServer.build());
            }
        }
        ackStatus.setStatus(status);
        MsgServerToClient.Builder msgAck = MsgServerToClient.newBuilder();
        msgAck.setMsgStatus(ackStatus.build());
        sendServerMsg(msgAck.build());
    }

    private void HandleGetUserInfo(byte[] username) {
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        ClientInfo.Builder clientInfo = ClientInfo.newBuilder();
        if (assets.userExists(username)) {
            clientInfo.setUsername(ByteString.copyFrom(username));
            clientInfo.setKey(ByteString.copyFrom(assets.getKey(username)));
            clientInfo.setOnline(false);
            for (UserConn userConn : server.getActiveConnections()) {
                if (Arrays.equals(userConn.getUsername().getBytes(), username)) {
                    clientInfo.setOnline(true);
                }
            }
        } else {
            clientInfo.setOnline(false);
            clientInfo.setUsername(ByteString.copyFromUtf8(""));
            clientInfo.setKey(ByteString.copyFromUtf8(""));
        }
        msg.addUsers(clientInfo.build());
        sendServerMsg(msg.build());
    }


    private Boolean HandleUpdateKey(byte[] username, byte[] key) {
        if (getUsername().getBytes().equals(username)) {
            return assets.setKey(username, key);
        }
        return false;
/*
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        msg.set // TODO: ADD UPDATE KEY SUCCESS
        sendServerMsg(msg.build());
*/
    }

    private void sendServerMsg(MsgServerToClient msg){
        // TODO: lock
        printPretty("send message {" + msg.toString() + "}");
        try {
            outputStream.writeInt(msg.getSerializedSize());
        } catch (IOException e) {
            printPretty(e.toString()  + ":" + e.getMessage());
        }
        try {
            outputStream.write(msg.toByteArray());
        } catch (IOException e) {
            printPretty(e.toString()  + ":" + e.getMessage());
        }
    }

    //called repeatedly for many objects in the same stream.
    private MsgClientToServer recvClientMsg(){
        // TODO: lock
        int length = 0;
        try {
            length = inputStream.readInt();
        } catch (IOException e) {
            printPretty(e.toString()  + ":" + e.getMessage());
            return null;
        }
        byte[] buf = new byte[length];
        try {
            inputStream.readFully(buf);
        } catch (IOException e) {
            printPretty(e.toString()  + ":" + e.getMessage());
            return null;
        }
        MsgClientToServer msg = null;
        try {
            msg = MsgClientToServer.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            printPretty(e.toString()  + ":" + e.getMessage());
            return null;
        }
        return msg;
    }

    protected void printPretty(String s) {
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("[hh:mm:ss]");
        System.out.println(ft.format(dNow) + " [" + getUsername() + "]: " + s);
    }
}
