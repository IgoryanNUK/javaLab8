package app.server;

import app.exceptions.RequestReadingException;
import app.exceptions.UnknownException;
import app.messages.requests.*;
import app.messages.response.AccessResp;
import app.messages.response.MessageResp;
import app.messages.response.ProductsResp;
import app.messages.response.Response;
import app.product.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.List;

public class RequestHandler {
    private CollectionManager collection;

    public RequestHandler(CollectionManager collection) {
        this.collection = collection;
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
            case REMOVE -> removeIf((RemoveReq) req);
            case GET -> getIf((GetReq) req);
            case INFO -> getInfo();
            case ADD -> add((AddReq) req);
            case UPDATE -> update((AddReq) req);
            case REG -> reg((RegistrationReq) req);
            case AUTH -> auth((AuthReq) req);
            default -> new MessageResp("Ошибка чтения комманды");
        };
    }

    /**
     * Удаление всех объектов, уодвлетворящих заданному условию.
     *
     * @param req запрос, содержащий условие удаления объекта
     * @return ответ, содержащий отчет об удалении объекта
     */
    public Response removeIf(RemoveReq req) {
        String message;
        if (collection.removeIf(req.getPredicate())) {
            message = "Продукт(ы) успешно удален(ы).";
        } else {
            message = "Не нашёл подходящих продуктов(.";
        }
        return new MessageResp(message);
    }

    /**
     * Получение продуктов коллекции, удовлетворяющих заданному условию.
     *
     * @param req запрос, содержащий условие фильтрации коллекции.
     * @return ответ, содержащий список продуктов
     */
    public Response getIf(GetReq req) {
        List<Product> list = collection.getIf(req.getPredicate());
        return new ProductsResp(list);
    }

    /**
     * Получение информации о коллекции.
     *
     * @return ответ, содержащий строковую информацию о коллекции.
     */
    public Response getInfo() {
        String resp = "Данные о коллекции:" +
                "\nтип: " + collection.getCollectionName() +
                "\nколичество элементов: " + collection.getSize();

        return new MessageResp(resp);
    }

    /**
     * Добавление продукта в коллекцию по соответствующему запросу.
     *
     * @param req запрос, содержащий информацию о продукте.
     * @return
     */
    private Response add(AddReq req) {
        String jsonMessage = req.getJsonMessage();

        ObjectMapper oM = new ObjectMapper();
        String partNumber;
        Double manufactureCost;
        String name;
        double x;
        double y;
        float price;
        UnitOfMeasure unitOfMeasure;
        String ownerName;
        float height;
        Color eyeColor;
        Country nationality;
        try {
            JsonNode jsonNode = oM.readTree(jsonMessage);

            name = jsonNode.get("name").asText();
            x = jsonNode.get("x").asDouble();
            y = jsonNode.get("y").asDouble();
            price = (float) jsonNode.get("price").asDouble();

            partNumber = jsonNode.get("partNumber").asText();

            manufactureCost = jsonNode.get("manufactureCost").asDouble();

            String unitOfMeasureString = jsonNode.get("unitOfMeasure").asText();
            unitOfMeasure = getEnum(unitOfMeasureString, UnitOfMeasure.values());

            ownerName = jsonNode.get("ownerName").asText();

            height = (float) jsonNode.get("height").asDouble();

            String eyeColorString = jsonNode.get("eyeColor").asText();
            eyeColor = getEnum(eyeColorString, Color.values());

            String nationalityString = jsonNode.get("nationality").asText();
            nationality = getEnum(nationalityString, Country.values());

        } catch (Exception e) {
            throw new RequestReadingException(RequestType.ADD, e);
        }

        String mes;
        if (collection.add(req.getLogin(), req.getPassword(),new Product(0, name, new Coordinates(x, y), price, partNumber, manufactureCost,
                unitOfMeasure, new Person(ownerName, height, eyeColor, nationality)))) {
            mes = "***Продукт " + name + " успешно добавлен в коллекцию***";
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
        UnitOfMeasure unitOfMeasure;
        String ownerName;
        float height;
        Color eyeColor;
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

            String unitOfMeasureString = jsonNode.get("unitOfMeasure").asText();
            unitOfMeasure = getEnum(unitOfMeasureString, UnitOfMeasure.values());

            ownerName = jsonNode.get("ownerName").asText();

            height = (float) jsonNode.get("height").asDouble();

            String eyeColorString = jsonNode.get("eyeColor").asText();
            eyeColor = getEnum(eyeColorString, Color.values());

            String nationalityString = jsonNode.get("nationality").asText();
            nationality = getEnum(nationalityString, Country.values());

        } catch (Exception e) {
            throw new RequestReadingException(RequestType.ADD, e);
        }

        Product product = collection.getIf(p -> p.getId() == id).get(0);
        if (product == null) {
            return new MessageResp("Не нашёл продукта с указанным id(");
        }

        product.setName(name);
        product.setCoordinates(new Coordinates(x, y));
        product.setPartNumber(partNumber);
        product.setManufactureCost(manufactureCost);
        product.setUnitOfMeasure(unitOfMeasure);
        product.setPrice(price);
        product.setOwner(new Person(ownerName, height, eyeColor, nationality));
        return new MessageResp("***Данные продукта "+ name + " успешно обновлены***");

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


    private Response reg(RegistrationReq req) {
        try {
            String login = req.getLogin();
            String password = req.getPassword();

            if (collection.register(req.getLogin(), req.getPassword())) {
                return new AccessResp(login, password);
            } else {
                return new MessageResp("Не удалось зарегистрировать пользователя. Данный логин жуе занят.");
            }
        } catch (Exception e) {
            throw new UnknownException(e);
        }

    }

    private Response auth(AuthReq req) {
        String login = req.getLogin();
        String password = req.getPassword();

        if (collection.auth(login, password)) {
            return new AccessResp(login, password);
        } else {
            return new MessageResp("Ошибка авторизации. Указан неправильный логин или пароль.");
        }
    }
}
