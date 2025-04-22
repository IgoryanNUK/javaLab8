package app.messages.commands;

import app.client.UserIOManager;
import app.messages.requests.GetReq;
import app.messages.requests.Request;

public class Show extends Command {
    {
        name = "show";
        description = "Выводит все продукты коллекции.";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        return new GetReq(e -> true);
    }
}
