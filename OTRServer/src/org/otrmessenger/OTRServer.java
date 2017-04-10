package org.otrmessenger;

import javax.net.ssl.SSLSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OTRServer {
    private AssetHandler assets;
    private List<UserConn> activeConnections;
    OTRServer() {
        assets = new AssetHandler();
        activeConnections = Collections.synchronizedList(new ArrayList<UserConn>());
    }

    public void ListenAndServe() {
        try {
            ServerSocket server = new ServerSocket(10050);
            while (true) {
                Socket clientSock = server.accept();
                UserConn userConn = new UserConn(clientSock);
                userConn.run();
            }
        }
        catch (Exception e) {
            System.err.println("Exception caught:" + e);
        }
    }
    private void Startup() {

    }
    private void HandleNewConn(SSLSocket sock) {

    }
    private void HandleNewConn(Socket sock) {

    }
   }

