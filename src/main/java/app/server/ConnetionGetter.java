package app.server;

import java.net.ServerSocket;
import java.net.Socket;

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
