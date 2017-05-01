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
    protected AssetHandler assets;
    protected List<UserConn> activeConnections;
    protected ServerState state;
    protected int portNumber;

    private static OTRServer instance = null;
    protected OTRServer() {
        assets = new AssetHandler();
        activeConnections = Collections.synchronizedList(new ArrayList<UserConn>());
        portNumber = 10050;
        state = ServerState.SERVER_UNKNOWN;
    }

    public static OTRServer getInstance() {
        if(instance == null) {
            instance = new OTRServer();
        }
        return instance;
    }

    public void Launch() {
        if (state != ServerState.SERVER_LAUNCHED) {
            state = ServerState.SERVER_LAUNCHED;
            try {
                ServerSocket server = new ServerSocket(portNumber);
                while (state == ServerState.SERVER_LAUNCHED) {
                    Socket clientSock = server.accept();
                    UserConn userConn = new UserConn(clientSock);
                    activeConnections.add(userConn);
                    Thread t = new Thread(userConn);
                    t.start();
                }
            } catch (Exception e) {
                System.err.println("Exception caught:" + e);
            }
        }
    }

    public void Stop() {
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

    public void Reset() {
        Stop();
        this.assets.reset();
    }

    public AssetHandler getAssets() {
        return assets;
    }

    public List<UserConn> getActiveConnections() {
        return activeConnections;
    }

    public ServerState getState() {
        return state;
    }
}
