package app.messages.commands;

import app.client.UserIOManager;
import app.messages.requests.InfoReq;
import app.messages.requests.Request;

/**
 * Команда, выводящая информацию о коллекции.
 */
public class Info extends Command {
    {
        name = "info";
        description = "Выводит основную информацию о коллекции";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        return new InfoReq();
    }
 }
