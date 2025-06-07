package app.server;

import app.exceptions.RequestReadingException;
import app.exceptions.UnknownException;
import app.messages.requests.*;
import app.messages.response.AccessResp;
import app.messages.response.MessageResp;
import app.messages.response.ProductsResp;
import app.messages.response.Response;
import app.product.*;
import app.server.database.Database;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler extends Thread {
    private final Request request;
    private final Database database;
    private final ExecutorService sendPool;
    private final Socket sock;

    public RequestHandler(Request request, Database database, ExecutorService sendPool, Socket sock) {
        this.database = database;
        this.request = request;
        this.sock = sock;
        this.sendPool = sendPool;
    }


    @Override
    public void run() {
        try {
            Response resp = handleRequest(request);
            Runnable sender = new ResponseSender(resp, sock);
            sendPool.execute(sender);
        } catch (Exception e) {
            throw new UnknownException(e);
        }
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
        if (database.removeIf(req.getPredicate(), req.getLogin(), req.getPassword())) {
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
    public Response getIf(GetReq req) {
        List<Product> list = database.getIf(req.getPredicate());
        return new ProductsResp(list);
    }

    /**
     * Получение информации о коллекции.
     *
     * @return ответ, содержащий строковую информацию о коллекции.
     */
    @Transactional
    public Response getInfo() {
        String resp = "В базе данных находятся: " + database.getProductsCount() + " продуктов.";

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
        String unitOfMeasureString;
        String ownerName;
        float height;
        String eyeColorString;
        Color eyeColor;
        Country nationality;
        String nationalityString;
        try {
            JsonNode jsonNode = oM.readTree(jsonMessage);

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

        Product p = new Product(name, x, y, price, partNumber, manufactureCost, unitOfMeasureString,
                ownerName, height, eyeColorString, nationalityString);

        String mes;
        if (database.addProduct(p, req.getLogin(), req.getPassword())) {
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
        int result = database.updateProduct(p, req.getLogin(), request.getPassword());
        if (result == 1) {
            message = "***Данные продукта "+ name + " успешно обновлены***";
        } else if (result == -1) {
            message = "***Отказано в доступе к продукту с данным id***";
        } else {
            message = "Не нашёл продукта с указанным id(";
        }
        return new MessageResp(message);

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

            if (database.register(req.getLogin(), req.getPassword())) {
                return new AccessResp(login, password);
            } else {
                return new MessageResp("Не удалось зарегистрировать пользователя. Данный логин уже занят.");
            }
        } catch (Exception e) {
            throw new UnknownException(e);
        }

    }

    private Response auth(AuthReq req) {
        String login = req.getLogin();
        String password = req.getPassword();

        if (database.auth(login, password)) {
            return new AccessResp(login, password);
        } else {
            return new MessageResp("Ошибка авторизации. Указан неправильный логин или пароль.");
        }
    }
}
