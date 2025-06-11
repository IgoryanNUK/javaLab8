package app.server.services;

import app.exceptions.RequestReadingException;
import app.exceptions.UnknownException;
import app.messages.requests.AddReq;
import app.messages.requests.RemoveReq;
import app.messages.requests.Request;
import app.messages.requests.RequestType;
import app.messages.response.Response;
import app.product.*;
import app.server.database.Database;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.List;
import java.util.function.Predicate;

@Service
public class RequestHandler {
    private final Database database;

    public RequestHandler(Database database) {
        this.database = database;
    }

    /**
     * Обработка запроса.
     *
     * @param req запрос
     * @return ответ
     */
    public Response handleRequest(Request req) {
        RequestType type = req.getType();

        return switch (type) {
            case UPDATE -> update((AddReq) req);
            default -> null;
        };
    }

    /**
     * Удаление всех объектов, уодвлетворящих заданному условию.
     *
     * @param req запрос, содержащий условие удаления объекта
     * @return ответ, содержащий отчет об удалении объекта
     */
    public MessageResp removeIf(Predicate<Product> pred, UserEntity user) {
        String message;

        String password = hashPassword(user.getPassword());
        if (database.removeIf(pred, user.getName(), password)) {
            message = "Продукт(ы) успешно удален(ы).";
        } else {
            message = "Не нашёл подходящих продуктов, которые вы имеете право удалить.";
        }
        return new MessageResp(message);
    }

    /**
     * Получение продуктов коллекции, удовлетворяющих заданному условию.
     *
     * @param req запрос, содержащий условие фильтрации коллекции.
     * @return ответ, содержащий список продуктов
     */
    public List<Product> getIf(Predicate<Product> pred) {
        List<Product> list = database.getIf(pred);
        return list;
    }

    /**
     * Получение информации о коллекции.
     *
     * @return ответ, содержащий строковую информацию о коллекции.
     */
    public MessageResp getInfo() {
        String resp = "В базе данных находятся: " + database.getProductsCount() + " продуктов.";

        return new MessageResp(resp);
    }

    /**
     * Добавление продукта в коллекцию по соответствующему запросу.
     *
     * @param req запрос, содержащий информацию о продукте.
     * @return
     */
    public MessageResp add(Product product, UserEntity user) {
        String login = user.getName();
        String password = hashPassword(user.getPassword());

        String mes;
        System.out.println(product + " " + user);
        if (database.addProduct(product, login, password)) {
            mes = "***Продукт " + product.getName() + " успешно добавлен в коллекцию***";
        } else {
            mes = "***Не удалось добавить объект в коллекцию***";
        }

        return new MessageResp(mes);


    }

    /**
     * Обновляет характеристики продукта по ууказанному id.
     *
     * @param req запрос, который должен содержать id продукта и его характеристики.
     * @return
     */
    private Response update(AddReq req) {
        String jsonMessage = req.getJsonMessage();

        ObjectMapper oM = new ObjectMapper();
        int id;
        String partNumber;
        Double manufactureCost;
        String name;
        double x;
        double y;
        float price;
        String unitOfMeasureString;
        UnitOfMeasure unitOfMeasure;
        String ownerName;
        float height;
        String eyeColorString;
        Color eyeColor;
        String nationalityString;
        Country nationality;
        try {
            JsonNode jsonNode = oM.readTree(jsonMessage);

            id = jsonNode.get("id").asInt();
            name = jsonNode.get("name").asText();
            x = jsonNode.get("x").asDouble();
            y = jsonNode.get("y").asDouble();
            price = (float) jsonNode.get("price").asDouble();

            partNumber = jsonNode.get("partNumber").asText();

            manufactureCost = jsonNode.get("manufactureCost").asDouble();

            unitOfMeasureString = jsonNode.get("unitOfMeasure").asText();
            unitOfMeasure = getEnum(unitOfMeasureString, UnitOfMeasure.values());

            ownerName = jsonNode.get("ownerName").asText();

            height = (float) jsonNode.get("height").asDouble();

            eyeColorString = jsonNode.get("eyeColor").asText();
            eyeColor = getEnum(eyeColorString, Color.values());

            nationalityString = jsonNode.get("nationality").asText();
            nationality = getEnum(nationalityString, Country.values());

        } catch (Exception e) {
            throw new RequestReadingException(RequestType.ADD, e);
        }

        Product p = new Product(name, x, y, price, partNumber, manufactureCost,
                unitOfMeasureString, ownerName, height, eyeColorString, nationalityString);
        p.setId(id);

        String message;
        int result = database.updateProduct(p, req.getLogin(), req.getPassword());
        if (result == 1) {
            message = "***Данные продукта "+ name + " успешно обновлены***";
        } else if (result == -1) {
            message = "***Отказано в доступе к продукту с данным id***";
        } else {
            message = "Не нашёл продукта с указанным id(";
        }
        return null;

    }

    /**
     * Получение enum объекта по его названию.
     *
     * @param string название enum значения
     * @param values все возиожные значения enum
     * @return
     * @param <E>
     */
    private <E extends Enum<E>> E getEnum(String string, E[] values) {
        for (E val : values) {
            if (val.toString().equals(string)) return val;
        }
        return null;
    }


    public String reg(UserEntity user) {
        String password = hashPassword(user.getPassword());
        String login = user.getName();

        try {
            if (database.register(login, password)) {
                return "зарегано";
            } else {
                return "лох объелся блох";
            }
        } catch (Exception e) {
            throw new UnknownException(e);
        }

    }

    private String hashPassword(String original) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return new String(md.digest(original.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MessageResp auth(UserEntity user) {
        String password = hashPassword(user.getPassword());
        String login = user.getName();

        if (database.auth(login, password)) {
            return new MessageResp("зарегано");
        } else {
            return new MessageResp("лох объелся блох");
        }
    }
}
