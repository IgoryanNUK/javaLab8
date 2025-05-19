package app.server;


import app.server.config.ApplicationConfiguration;
import app.server.config.FlywayConfig;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Component
public class Server {
    private final int port = 4027;
    private ServerSocket server;
    private ConnetionGetter connection;

    @Autowired
    private CollectionManager collection;
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

    public Server() {
        try {
            server = new ServerSocket(port);

            System.out.println("*authorisation succeed*");

            connection = new ConnetionGetter(server);
            logger.info("Start success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Server start error: ", e);
            isRunning = false;
        }
    }

    private String[] authorisation() {
        Console console = System.console();
        String login = console.readLine("Login: ");
        String password = new String(console.readPassword("Password: "));
        return new String[]{login, password};
    }

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class,
                FlywayConfig.class);

        Flyway f = context.getBean(Flyway.class);
        System.out.println(f.info());
        Server server = context.getBean(Server.class);
        server.run();
    }

    /**
     * Запуск приложения
     */
    public void run() {
        if (isRunning) {
            try {
                for (int i = 0; i < 5; i++) {
                    RequestGetter getter = new RequestGetter(connection, collection, executePool, sendPool);
                    readPool.execute(getter);
                }

                while (isRunning) {
                    String req = console.readLine();
                    System.out.println(req);
                    if (req.equals("x")) {
                        isRunning = false;
                        readPool.shutdownNow();
                        executePool.shutdownNow();
                        sendPool.shutdownNow();
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
