package app.messages.commands;

import app.client.UserIOManager;
import app.exceptions.UnknownException;
import app.exceptions.WrongCommandFormat;
import app.messages.requests.AddReq;
import app.messages.requests.Request;
import app.messages.requests.RequestType;
import app.product.Coordinates;
import app.product.Person;
import app.product.UnitOfMeasure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static app.client.EnterManager.*;

public class Update extends Command {
    {
        name = "update {id}";
        description = "Обновляет все характеристики продукта по заданному id";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {
        if (args.length != 2) throw new WrongCommandFormat(name);

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch(NumberFormatException f) { throw new WrongCommandFormat(name);}

        ObjectMapper oM = new ObjectMapper();
        ObjectNode jsonNode = oM.createObjectNode();
        boolean isScript = ioManager.isScript();
        try {
            jsonNode.put("id", id);
            jsonNode.put("name", nameEnter(ioManager, "название продукта", isScript));
            Coordinates c = coordinatesEnter(ioManager, isScript);
            jsonNode.put("x", c.getX());
            jsonNode.put("y", c.getY());

            jsonNode.put("price", floatEnter(ioManager, "стоимость продукта", isScript));
            jsonNode.put("partNumber", partNumberEnter(ioManager, isScript));
            jsonNode.put("manufactureCost", manufactureCostEnter(ioManager, isScript));
            UnitOfMeasure uom = enumEnter(ioManager, UnitOfMeasure.values(), "единица измерения", false, isScript);
            jsonNode.put("unitOfMeasure", uom.toString());
            Person p = personEnter(ioManager, isScript);
            jsonNode.put("ownerName", p.getName());
            jsonNode.put("height", p.getHeight());
            jsonNode.put("eyeColor", p.getEyeColor().toString());
            jsonNode.put("nationality", p.getNationality().toString());

        } catch (Exception e) {
            throw new UnknownException(e);
        }

        return new AddReq(RequestType.UPDATE, jsonNode.toString());
    }
}
