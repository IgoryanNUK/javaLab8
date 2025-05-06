package app.client;

import app.exceptions.UnavaluableServer;
import app.messages.requests.Request;
import app.messages.response.AccessResp;
import app.messages.response.MessageResp;
import app.messages.response.ProductsResp;
import app.messages.response.Response;
import app.product.Product;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.stream.Collectors;

public class ClientConnectionManager {
    private final int port;
    private final InetAddress host;
    private final Client app;

    public static void main(String[] args) {
        /*Object o = new Object();
        try {
            ClientConnectionManager cc = new ClientConnectionManager(4027, "Igoryan-Laptop");
            byte[] arr = cc.serializeRequest(new Command() {
                @Override
                public String getName() {return "Прошло успешно!";}
            });
            for (byte a : arr) {
                System.out.println(a);
            }
        } catch (Exception e) {}*/
    }


    /**
     * Конструктор, определяющий сервер, с которым будет проводиться подклбчение.
     *
     * @param port порт сервера
     * @param hostName имя хоста сервера
     * @throws Exception непредвиденная ошибка
     */
    public ClientConnectionManager(int port, String hostName, Client app) throws Exception {
        this.port = port;
        host = InetAddress.getLocalHost();
        this.app = app;
    }


    /**
     * Отправляет запрос серверу.
     *
     * @param req объект запроса
     * @throws Exception непредвиденая ошибка
     * @return ответ сервера
     */
    public String askServer(Request req) throws Exception {
        InetSocketAddress address = new InetSocketAddress(host, port);
        byte[] reqArray = serializeRequest(req);

        try (SocketChannel sock = SocketChannel.open()) {
            //установка соединения и настройка сокета
            sock.connect(address);
            sock.configureBlocking(false);

            ByteBuffer buff = ByteBuffer.wrap(reqArray);
            sock.write(buff);

            return readServerResponse(sock);
        } catch (java.net.ConnectException cE) {
            throw new UnavaluableServer();
        }
    }

    /**
     * Сериализует запросы.
     *
     * @param req объект запроса
     * @return массив байтов
     */
    private byte[] serializeRequest(Request req) {
        ByteArrayOutputStream bA = new ByteArrayOutputStream();
        try(ObjectOutputStream oS = new ObjectOutputStream(bA)) {
            oS.writeObject(req);
        } catch (Exception e) {}

        return bA.toByteArray();
    }

    private String readServerResponse(SocketChannel sock) throws Exception {
        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead =sock.read(responseBuffer)) != -1) {
            responseBuffer.flip();
            byte[] bytes = new byte[bytesRead];
            responseBuffer.get(bytes);
            baos.write(bytes);
            responseBuffer.clear();
        }

        byte[] responseBytes = baos.toByteArray();
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(responseBytes))) {
            Response r = (Response) ois.readObject();
            return handleResponse(r);
        }
    }

    private String handleResponse(Response resp) {
        return switch(resp.getType()) {
            case MESSAGE -> ((MessageResp) resp).getMessage();
            case PRODUCTS -> ((ProductsResp) resp).getProducts().stream().map(Product::print).collect(Collectors.joining("\n"));
            case ACCESS -> {
                AccessResp ar = (AccessResp) resp;
                String l = ar.getLogin();
                String p = ar.getPassword();
                app.setAccess(l, p);
                yield "Вы успешно авторизованы.";
            }
            default -> "Ошибка чтения ответа";
        };
    }
}
