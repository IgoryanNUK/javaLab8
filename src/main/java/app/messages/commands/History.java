package app.messages.commands;

import app.client.CommandManager;
import app.client.UserIOManager;
import app.messages.requests.Request;

public class History extends Command {
    private CommandManager cm;

    {
        name = "history";
        description = "Выводит крайние 14 введённых команд";
    }

    public History(CommandManager cm) {
        this.cm = cm;
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        cm.showHistory(ioManager);
        return null;
    }
}
