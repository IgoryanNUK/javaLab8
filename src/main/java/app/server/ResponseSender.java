package app.server;

import app.exceptions.UnknownException;
import app.messages.response.Response;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseSender extends Thread {
    private ConcurrentHashMap<String, Response> responses;
    private Socket sock;
    private String id;

    public ResponseSender(ConcurrentHashMap<String, Response> responses, Socket sock, String id) {
        this.id = id;
        this.responses = responses;
        this.sock = sock;
    }

    @Override
    public void run() {
        try {
            Response resp;
            synchronized (responses) {
                while (responses.isEmpty()) responses.wait();
                resp = responses.remove(id);
                responses.notifyAll();
            }

            synchronized (sock) {
                send(resp, sock);
            }
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
