package org.otrmessenger;

import org.otrmessenger.messaging.Messaging.ServerState;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class OTRServer {
    private AssetHandler assets;
    private List<UserConn> activeConnections;
    private ServerState state;
    protected int portNumber;

    private static OTRServer instance = null;
    protected OTRServer() {
        assets = new AssetHandler();
        activeConnections = Collections.synchronizedList(new ArrayList<UserConn>());
        portNumber = 10050;
    }

    public static OTRServer getInstance() {
        if(instance == null) {
            instance = new OTRServer();
        }
        return instance;
    }

    public void Launch() {
        state = ServerState.SERVER_LAUNCHED;
        try {
            ServerSocket server = new ServerSocket(portNumber);
            while (state == ServerState.SERVER_LAUNCHED) {
                Socket clientSock = server.accept();
                UserConn userConn = new UserConn(clientSock);
                activeConnections.add(userConn);
                userConn.run();
            }
        } catch (Exception e) {
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

    void Stop() {
        state = ServerState.SERVER_STOPPED;
        for (UserConn conn : activeConnections) {
            if (!conn.isAdmin()) {
                try {
                    conn.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                activeConnections.remove(conn);
            }
        }
        // TODO: consider listening for admin connections
    }

    void Reset() {
        Stop();
        this.assets.reset();
    }
}
