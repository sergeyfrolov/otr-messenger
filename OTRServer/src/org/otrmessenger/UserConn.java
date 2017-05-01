package org.otrmessenger;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
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
                        server.getActiveConnections().remove(this);
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

                if (clientMsg.hasUpdatedSignKey()) {
                    ByteString bstrKey = clientMsg.getUpdatedSignKey();
                    HandleUpdateSignKey(getUsername().getBytes(), bstrKey.toByteArray());
                }

                if (clientMsg.hasUpdatedEncryptionKey()) {
                    ByteString bstrKey = clientMsg.getUpdatedEncryptionKey();
                    HandleUpdateEncryptionKey(getUsername().getBytes(), bstrKey.toByteArray());
                }

                if (clientMsg.hasRequestKeyPairChange()) {
                    Boolean wat = clientMsg.getRequestKeyPairChange();
                    // TODO: wat?
                }
            }
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    //SSLSocket getSocket() {
    public Socket getSocket() {
        return this.sock;
    }

    public void setSocket(Socket sock) {
        this.sock = sock;
    }

    public Boolean isAdmin() {
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
                    clientInfo.setSignKey(ByteString.copyFrom(assets.getSignKey(username)));
                    msg.addUsers(clientInfo.build());
                }
                break;
            case GET_ALL_USERS:
                for (byte[] username : assets.getUsers()) {
                    ClientInfo.Builder clientInfo = ClientInfo.newBuilder();
                    clientInfo.setUsername(ByteString.copyFrom(username));
                    msg.addUsers(clientInfo.build());
                }
                break;
            case GET_ONLINE_USERS:
                for (UserConn userConn : server.getActiveConnections()) {
                    ClientInfo.Builder clientInfo = ClientInfo.newBuilder();
                    clientInfo.setUsername(ByteString.copyFrom(userConn.getUsername().getBytes()));
                    msg.addUsers(clientInfo.build());
                }
                break;
            case GET_CURRENT_STATE:
                msg.setState(server.getState());
                break;
            case LAUNCH:
                server.Launch();
                msg.setState(server.getState());
                break;
            case RESET:
                server.Reset();
                msg.setState(server.getState());
                break;
            case STOP:
                server.Stop();
                msg.setState(server.getState());
                break;
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

                MsgServerToClient.Builder msgFromServer = MsgServerToClient.newBuilder();
                msgFromServer.setMsg(msgFromUser);

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
            byte[] sign_key = assets.getSignKey(username);
            if (sign_key != null) {
                clientInfo.setSignKey(ByteString.copyFrom(sign_key));
            }
            byte[] enc_key = assets.getEncryptionKey(username);
            if (enc_key != null) {
                clientInfo.setEncryptionKey(ByteString.copyFrom(enc_key));
            }
            clientInfo.setOnline(false);
            for (UserConn userConn : server.getActiveConnections()) {
                if (Arrays.equals(userConn.getUsername().getBytes(), username)) {
                    clientInfo.setOnline(true);
                }
            }
        } else {
            clientInfo.setOnline(false);
            clientInfo.setUsername(ByteString.copyFromUtf8(""));
            clientInfo.setSignKey(ByteString.copyFromUtf8(""));
        }
        msg.addUsers(clientInfo.build());
        sendServerMsg(msg.build());
    }

    private Boolean HandleUpdateSignKey(byte[] username, byte[] key) {
        boolean success = false;
        if(Arrays.equals(username, getUsername().getBytes())) {
            success = assets.setSignKey(username, key);
        }
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        msg.setKeyUpdateSuccess(success);
        sendServerMsg(msg.build());
        return success;
    }

    private Boolean HandleUpdateEncryptionKey(byte[] username, byte[] key) {
        boolean success = false;
        if(Arrays.equals(username, getUsername().getBytes())) {
            success = assets.setEncryptionKey(username, key);
        }
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        msg.setKeyUpdateSuccess(success);
        sendServerMsg(msg.build());
        return success;
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
