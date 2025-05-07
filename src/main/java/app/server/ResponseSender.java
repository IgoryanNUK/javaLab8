package app.server;

import app.exceptions.UnknownException;
import app.messages.response.Response;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class ResponseSender extends Thread {
    private final Response response;
    private final Socket sock;

    public ResponseSender(Response resp, Socket sock) {
        this.response = resp;
        this.sock = sock;
    }

    @Override
    public void run() {
        try {
            send(response, sock);
        } catch (Exception e) {
            throw new UnknownException(e);
        }
    }

    /**
     * Отправляет ответ клиенту.
     *
     * @param resp объект ответа
     * @param sock сокет соединения с клиентом
     * @throws Exception
     */
    public static void send(Response resp, Socket sock) throws Exception {
        OutputStream os = sock.getOutputStream();

        ObjectOutputStream ous = new ObjectOutputStream(os);

        ous.reset();
        ous.writeObject(resp);
        ous.flush();
        ous.close();
    }
}
