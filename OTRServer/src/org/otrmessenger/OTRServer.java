package org.otrmessenger;

import org.otrmessenger.messaging.Messaging.ServerState;

import javax.net.ssl.SSLSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class OTRServer {
    private AssetHandler assets;
    private List<UserConn> activeConnections;
    private ServerState state;

    private static OTRServer instance = null;
    protected OTRServer() {
        assets = new AssetHandler();
        activeConnections = Collections.synchronizedList(new ArrayList<UserConn>());
        state = ServerState.SERVER_LAUNCHED;
    }

    public static OTRServer getInstance() {
        if(instance == null) {
            instance = new OTRServer();
        }
        return instance;
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
    public AssetHandler getAssets() {
        return assets;
    }

    public List<UserConn> getActiveConnections() {
        return activeConnections;
    }

    ServerState getState() {
        return state;
    }

    void Launch() {
        // TODO:
    }

    void Terminate() {
        // TODO
    }

    void Reset() {
        // TODO
    }
   }

