package app.messages.commands;

import app.client.UserIOManager;
import app.exceptions.WrongCommandFormat;
import app.messages.requests.RemoveReq;
import app.messages.requests.Request;

public class RemoveLower extends Command {
    {
        name = "remove_lower {id}";
        description = "Удаляет все продукты, id которых меньше, чем заданный";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        if (args.length != 2) throw new WrongCommandFormat(name);

        try {
            int id = Integer.parseInt(args[1]);
            return new RemoveReq(p -> p.getId() < id);
        } catch (NumberFormatException f) {
            throw new WrongCommandFormat(name);
        }
    }
}
