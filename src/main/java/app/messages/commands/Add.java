package app.messages.commands;

import app.client.UserIOManager;
import app.exceptions.UnknownException;
import app.messages.requests.AddReq;
import app.messages.requests.Request;
import app.messages.requests.RequestType;
import app.product.Coordinates;
import app.product.Person;
import app.product.UnitOfMeasure;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static app.client.EnterManager.*;

public class Add extends Command {
    {
        name = "add";
        description = "Добавляет продукт в коллекцию";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager ioManager) {

        ObjectMapper oM = new ObjectMapper();
        ObjectNode jsonNode = oM.createObjectNode();
        boolean isScript = ioManager.isScript();
        try {
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
            String nat = p.getNationality() == null ? null : p.getNationality().toString();
            jsonNode.put("nationality", nat);

        } catch (Exception e) {
            throw new UnknownException(e);
        }
        return new AddReq(RequestType.ADD, jsonNode.toString());
    }
}
