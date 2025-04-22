package app.messages.commands;

import app.client.UserIOManager;
import app.exceptions.WrongCommandFormat;
import app.messages.requests.GetReq;
import app.messages.requests.Request;

/**
 * Выводит все продукты, партийный номер которых начинается с заданной строки.
 */
public class FilterStartsWithPN extends Command {
    {
        name = "filter_starts_with_pn {string}";
        description = "Выводит все продукты, партийный номер которых начинается с заданной строки";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {

        if (args.length != 2) throw new WrongCommandFormat(name);


        return new GetReq(p -> {
            String pn = p.getPartNumber();
            if (pn == null) return false;
            else return pn.startsWith(args[1]);
        });
    }

}
