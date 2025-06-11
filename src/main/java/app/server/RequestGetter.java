package app.server;

import app.messages.requests.Request;
import app.server.database.Database;
import app.server.services.RequestHandler;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class RequestGetter implements Runnable {
    private final ConnetionGetter connection;
    private final Database database;
    private final ExecutorService executePool;
    private final ExecutorService sendPool;

    public RequestGetter(ConnetionGetter connection, Database database, ExecutorService executePool, ExecutorService sendPool) {
        this.database = database;
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
                RequestHandler handler = new RequestHandler(database);
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
