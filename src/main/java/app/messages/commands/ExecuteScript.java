package app.messages.commands;

import app.client.CommandManager;
import app.client.UserIOManager;
import app.exceptions.FileNotFound;
import app.exceptions.KnownException;
import app.exceptions.UnknownException;
import app.exceptions.WrongCommandFormat;
import app.messages.requests.Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ExecuteScript  extends Command {
    private CommandManager commandManager;

    {
        name = "execute_script {path}";
        description = "Выполняет заранее записанный скрипт.";
    }

    public ExecuteScript(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        if (args.length < 2) throw new WrongCommandFormat(name);


        for (int i = 1; i < args.length; i++) {
            File file = new File(args[i]);
            if (!file.exists()) throw new FileNotFound("исполнение скрипта", args[i]);
            try (BufferedReader fileInput = new BufferedReader(new FileReader(file))) {
                UserIOManager scriptIO = new UserIOManager(fileInput, ioManager.getOutput(), true);
                while (fileInput.ready()) {
                    commandManager.handleCommand(scriptIO);
                }
            } catch (KnownException k) {
                throw k;
            }catch (Exception e) {
                throw new UnknownException(e);
            }
        }
        return null;
    }
}
