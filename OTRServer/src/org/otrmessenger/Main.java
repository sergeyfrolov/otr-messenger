package org.otrmessenger;

/**
 * Created by sfrolov on 4/8/17.
 */
public class Main {
    public static void main(String[] args) {
        OTRServer server = new OTRServer();
        server.ListenAndServe();
    }
}
