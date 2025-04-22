package app.server;

import app.messages.requests.Request;
import app.messages.response.Response;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Communicator {
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

    /**
     * Отправляет текстовый ответ клиенту.
     *
     * @param message текстовое сообщение
     * @param sock сокет соединения с клиентом
     * @throws Exception непредвиденная ошибка
     */
    public static void sendString(String message, Socket sock) throws Exception {
        OutputStream os = sock.getOutputStream();
        os.write(message.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
    }

    private static byte[] getSerSize(Object o) {
        ByteArrayOutputStream bA = new ByteArrayOutputStream();
        try(ObjectOutputStream oS = new ObjectOutputStream(bA)) {
            oS.writeObject(o);
        } catch (Exception e) {}

        int length = bA.toByteArray().length;
        System.out.println(length);

        return ByteBuffer.allocate(4).putInt(length).array();
    }
}
