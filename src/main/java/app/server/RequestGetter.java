package app.server;

import app.messages.requests.Request;
import app.messages.response.Response;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class RequestGetter implements Runnable {
    private final ConnetionGetter connection;
    private final CollectionManager collection;
    private final ExecutorService executePool;
    private final ExecutorService sendPool;

    public RequestGetter(ConnetionGetter connection, CollectionManager collection, ExecutorService executePool, ExecutorService sendPool) {
        this.collection = collection;
        this.connection = connection;
        this.sendPool = sendPool;
        this.executePool =executePool;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket sock = connection.getConnection();
                Request req = read(sock);
                RequestHandler handler = new RequestHandler(req, collection, sendPool, sock);
                executePool.execute(handler);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Возвращает полученный от клиента массив байтов.
     *
     * @param sock сокет соединения с клиентом
     * @return прочитанный объект
     * @throws Exception непредвиденные ошибки
     */
    public static Request read(Socket sock) throws Exception {
        InputStream is = sock.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        Request r = (Request) ois.readObject();
        return r;
    }
}
