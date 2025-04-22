package app.client;

import app.exceptions.UnknownException;

import java.io.BufferedReader;
import java.io.PrintStream;

public class UserIOManager {
    private final PrintStream output;
    private final BufferedReader input;
    private boolean isScript = false;

    public UserIOManager(BufferedReader input, PrintStream output) {
        this.output = output;
        this.input = input;
    }

    public UserIOManager(BufferedReader input, PrintStream output, boolean isScript) {
        this.output = output;
        this.input = input;
        this.isScript = isScript;
    }

    public PrintStream getOutput() {return output;}

    public BufferedReader getInput() {return input;}

    public boolean isScript() {return isScript;}

    public String ask(String request) {
        output.printf(request);
        try {
            return input.readLine();
        } catch (Exception e) {
            throw new UnknownException(e);
        }
    }

    public void println(String message) {
        output.println(message);
    }

    public void printf(String message) {
        output.printf(message);
    }
}
