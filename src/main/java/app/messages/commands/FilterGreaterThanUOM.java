package app.messages.commands;

import app.client.UserIOManager;
import app.exceptions.WrongCommandFormat;
import app.messages.requests.GetReq;
import app.messages.requests.Request;
import app.product.UnitOfMeasure;

public class FilterGreaterThanUOM extends Command {
    {
        name = "filter_greater_than_uom {unit_of_measure}";
        description = "Выводит все продукты, единица измерения которых больше заданного";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        if (args.length !=2) throw new WrongCommandFormat(name);

        final UnitOfMeasure request;
        for (UnitOfMeasure u : UnitOfMeasure.values()) {
            if (u.toString().equals(args[1])) {
                request = u;
                return new GetReq(p -> p.getUnitOfMeasure().compareTo(request) < 0);
            }
        }
        throw new WrongCommandFormat(name);
    }
}
