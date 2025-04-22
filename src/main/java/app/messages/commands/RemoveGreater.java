package app.messages.commands;

import app.client.UserIOManager;
import app.exceptions.WrongCommandFormat;
import app.messages.requests.RemoveReq;
import app.messages.requests.Request;

public class RemoveGreater extends Command {
    {
        name = "remove_greater {id}";
        description = "Удаляет все продукты, id которых больше, чем заданный.";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        if (args.length != 2) throw new WrongCommandFormat(name);

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException n) {
            throw new WrongCommandFormat(name);
        }

        return new RemoveReq(p -> p.getId() > id);
    }
}
