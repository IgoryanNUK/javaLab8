package app.messages.commands;

import app.client.CommandManager;
import app.client.UserIOManager;
import app.messages.requests.Request;

public class Help extends Command {
    {
        name = "help";
        description = "Выводит информацию о всех доступных командах";
    }
    private final CommandManager manager;

    public Help(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        manager.showCommands(ioManager);
        return null;
    }

}
