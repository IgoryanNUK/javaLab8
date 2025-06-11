package app.server;

import app.server.database.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Server {
    private final int port = 4027;
    private ServerSocket server;
    private ConnetionGetter connection;

    private Database database;
    private Logger logger;
    private final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    private boolean isRunning = true;

    private final int MAX_T = 5;
    private final ExecutorService readPool = Executors.newFixedThreadPool(MAX_T);
    private final ExecutorService executePool = Executors.newFixedThreadPool(MAX_T);
    private final ExecutorService sendPool = Executors.newCachedThreadPool();

    {
        try {
            LogManager.getLogManager().readConfiguration(Server.class.getResourceAsStream("/logging.properties"));
            logger = Logger.getLogger(Server.class.getName());
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
    }

    @Autowired
    public Server(Database database) {
        try {
            server = new ServerSocket(port);

            this.database = database;


            System.out.println("*authorisation succeed*");

            connection = new ConnetionGetter(server);


            for (int i = 0; i < 5; i++) {
                RequestGetter getter = new RequestGetter(connection, database, executePool, sendPool);
                readPool.execute(getter);
            }
            logger.info("Start success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Server start error: ", e);
            isRunning = false;
        }
    }

    public static void main(String[] args) {
    }
}
