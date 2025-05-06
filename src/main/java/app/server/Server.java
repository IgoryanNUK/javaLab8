package app.server;


import app.messages.requests.Request;
import app.messages.response.MessageResp;
import app.messages.response.Response;
import app.product.Color;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Server {
    private final int port = 4027;
    private ServerSocket server;
    private ConnetionGetter connection;
    private CollectionManager collection;
    private RequestHandler handler;
    private final String envVar = "LAB5_PATH";
    private Logger logger;
    private final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    private boolean isRunning = true;

    private int MAX_T = 5;
    private final ExecutorService readPool = Executors.newFixedThreadPool(MAX_T);
    private final ExecutorService executePool = Executors.newFixedThreadPool(MAX_T);
    private final ExecutorService sendPool = Executors.newCachedThreadPool();

    private final ConcurrentHashMap<String, Request> requests = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Response> responses = new ConcurrentHashMap<>();

    {
        try {
            LogManager.getLogManager().readConfiguration(Server.class.getResourceAsStream("/logging.properties"));
            logger = Logger.getLogger(Server.class.getName());
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
    }

    public Server() {
        try {
            server = new ServerSocket(port);

            String[] loginAndPassword = authorisation();
            collection = new CollectionManager(loginAndPassword[0], loginAndPassword[1]);
            System.out.println("*authorisation succeed*");

            connection = new ConnetionGetter(server);
            logger.info("Start success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Server start error: ", e);
        }
    }

    private String[] authorisation() {
        Console console = System.console();
        String login = console.readLine("Login: ");
        String password = new String(console.readPassword("Password: "));
        return new String[]{login, password};
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    private void run() {
        while (isRunning) {
            try {
                checkClosingRequest();

                Socket sock = connection.getConnection();
                if (sock.isBound()) {
                    try {
                        String id = Math.random()+"";
                        RequestGetter getter = new RequestGetter(sock, requests, id);
                        readPool.execute(getter);

                        RequestHandler handler = new RequestHandler(collection, requests, responses, id);
                        executePool.execute(handler);

                        ResponseSender sender = new ResponseSender(responses, sock, id);
                        sendPool.execute(sender);
                    } catch (Exception e) {
                        Communicator.send(new MessageResp("Произошла ошибка на сервере((((("),sock);
                        logger.log(Level.SEVERE, "Ошибка в работе сервера: ", e);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка в работе сервера: ", e);
            }
        }
    }

    private void checkClosingRequest() throws Exception{
        if (console.ready()) isRunning = false;
    }
}
