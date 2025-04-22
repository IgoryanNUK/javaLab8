package app.messages.commands;

import app.client.UserIOManager;
import app.messages.requests.RemoveReq;
import app.messages.requests.Request;

public class Clear extends Command {
    {
        name = "clear";
        description = "Удаляет все элементы коллекции";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        return new RemoveReq(p -> true);
    }
}
