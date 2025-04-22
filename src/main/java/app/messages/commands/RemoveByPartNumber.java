package app.messages.commands;

import app.client.UserIOManager;
import app.exceptions.WrongCommandFormat;
import app.messages.requests.RemoveReq;
import app.messages.requests.Request;

public class RemoveByPartNumber extends Command {
    {
        name = "remove_by_part_number {part_number}";
        description = "Удаляет продукт по заданному партийному номеру";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        if (args.length != 2) throw new WrongCommandFormat(name);

        return new RemoveReq(p -> {
            String pn = p.getPartNumber();
            if(pn == null) {return false;}
            else {return pn.equals(args[1]);}});
    }
}
