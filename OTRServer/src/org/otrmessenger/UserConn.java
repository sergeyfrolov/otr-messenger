package org.otrmessenger;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

;
import org.otrmessenger.messaging.Messaging.msgClientToServer;
import org.otrmessenger.messaging.Messaging.msgServerToClient;

import static sun.security.jgss.GSSToken.readInt;

/**
 * Created by sfrolov on 4/8/17.
 */
public class UserConn implements Runnable {
    protected String username;
    //protected SSLSocket sock;
    protected Socket sock;
    protected Boolean admin;
    protected Boolean loggedIn;

    UserConn(Socket sock) {
        this.sock = sock;
        this.username = "";
        this.admin = false;
        this.loggedIn = false;
    }

    @Override
    public void run() {
        try {
            InputStream input = this.sock.getInputStream();
            OutputStream output = this.sock.getOutputStream();
            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    this.serverText + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
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

    private void HandleLogin() {

    }

    private void HandleSignUp() {

    }

    private void HandleSend() {

    }

    private void HandleAskKey() {

    }

    private void HandleTerminate() {

    }

    private void HandleGetStats() {

    }

    private void HandleGetUserList() {

    }

    private void HandleGetUser() {

    }

    public static void sendServerMsg(OutputStream stream, msgServerToClient msg){
        ByteBuffer byteBuf = ByteBuffer.allocate(4 + msg.getSerializedSize());
        byteBuf.putInt(msg.getSerializedSize());
        byteBuf.put(msg.toByteArray());

        try {
            stream.write(byteBuf.array());
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    //called repeatedly for many objects in the same stream.
    public static msgClientToServer recvClientMsg(InputStream stream){
        //read protobuf header
        stream.readInt();
        int id = readInt(stream);
        int length = readInt(stream);

        //use header to interpret payload
        return readObject(id, length, stream);
    }
}
