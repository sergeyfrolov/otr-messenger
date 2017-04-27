package org.otrmessenger;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
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
            e.printStackTrace();
        }
            while (!sock.isClosed()) {
                MsgClientToServer clientMsg = recvClientMsg();
                if (clientMsg == null) {
                    // If errored during message recv(could be just end of connection)
                    try {
                        sock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                System.out.print("# Received message:" + clientMsg.toString());

                if (clientMsg.hasCredentials()) {
                    Credentials creds = clientMsg.getCredentials();
                    if (creds.getSignUp()) {
                        HandleSignUp(creds);
                    } else {
                        HandleLogin(creds);
                    }
                }

                // todo: if not logged in abort

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
        sendServerMsg(msg.build());
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
    }

    private void HandleSignUp(Credentials creds) {
        boolean cred_admin = creds.getAdmin();
        boolean success;
        if (cred_admin) {
            // No admin signing up
            success = false;
        } else {
            assets.addUser(creds.getUsername().toByteArray(),
                    creds.getPasswordHash().toByteArray());
            success = true;
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
            System.out.println("User " + getUsername() + " tried to spoof message from " +
                    msgFromUser.getFromUsername().toStringUtf8());
            // if we want to figure this out client-side, we can uncomment following return
            return;
        }
        Message.Builder message = Message.newBuilder();
        System.out.println("server.getActiveConnections()" + server.getActiveConnections());
        for (UserConn userConn : server.getActiveConnections()) {
            System.out.println("userConn" + userConn);
            if (userConn.getUsername().equals(msgFromUser.getToUsername().toStringUtf8())) {
                System.out.println("Found user");
                message.setToUsername(ByteString.copyFrom(username.getBytes()));
                message.setFromUsername(ByteString.copyFrom(msgFromUser.getToUsername().toStringUtf8().getBytes()));
                message.setText(msgFromUser.getText());
                message.setSignature(msgFromUser.getSignature());

                MsgServerToClient.Builder msgFromServer = MsgServerToClient.newBuilder();
                msgFromServer.setMsg(message);

                sendServerMsg(msgFromServer.build());
                return;
            }
        }
        // is user is offline, then:
        // TODO: send back something
    }

    private void HandleGetUserInfo(byte[] username) {
        MsgServerToClient.Builder msg = MsgServerToClient.newBuilder();
        ClientInfo.Builder clientInfo = ClientInfo.newBuilder();
        clientInfo.setUsername(ByteString.copyFrom(username));
        clientInfo.setKey(ByteString.copyFrom(assets.getKey(username)));
        clientInfo.setOnline(false);
        for (UserConn userConn : server.getActiveConnections()) {
            if (userConn.getUsername().equals(username.toString())) {
                clientInfo.setOnline(true);
            }
        }
        msg.addUsers(clientInfo.build());
        sendServerMsg(msg.build());
    }

    // Enforces Business Requirement to
    static public Boolean checkPasswordRequirements(String password) {
        // based on https://coderanch.com/t/583177/java/validate-string-characters-letter-number
        final Pattern letter = Pattern.compile("[a-zA-z]");
        final Pattern digit = Pattern.compile("[0-9]");
        final Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        return (password.length() >= 8)
                && special.matcher(password).find()
                && digit.matcher(password).find()
                && letter.matcher(password).find();
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
        System.out.print("% trying to send message:" + msg.toString());
        try {
            outputStream.writeInt(msg.getSerializedSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.write(msg.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //called repeatedly for many objects in the same stream.
    private MsgClientToServer recvClientMsg(){
        int length = 0;
        try {
            length = inputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        byte[] buf = new byte[length];
        try {
            inputStream.readFully(buf);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        MsgClientToServer msg = null;
        try {
            msg = MsgClientToServer.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
        return msg;
    }
}
