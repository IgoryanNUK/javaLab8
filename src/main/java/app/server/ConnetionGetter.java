package app.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ConnetionGetter {
    private ServerSocket serverSocket;

    public ConnetionGetter(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getConnection() throws Exception {
        try {
            return serverSocket.accept();
        } catch (Exception e) {
            throw e;
        }
    }


}
