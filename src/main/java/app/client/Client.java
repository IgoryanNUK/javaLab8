package app.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    private boolean isRunning = true;
    private final BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
    private final CommandManager commandManager = new CommandManager(this);
    private final UserIOManager ioManager = new UserIOManager(userInput, System.out);

    public static void main(String ... args) {
        Client client = new Client();
        client.run();
    }

    public void run() {
        while (isRunning) {
            try {
                commandManager.handleCommand(ioManager);
            } catch (Exception e) {
                ioManager.getOutput().println("0_0 " + e.getMessage());
            }
        }
    }

    public void stop() { isRunning = false; }
}
