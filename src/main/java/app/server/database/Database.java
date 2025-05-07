package app.server.database;

import app.exceptions.UnknownException;
import app.product.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private String user;
    private String password;
    private final String connectionAddress = "jdbc:postgresql://localhost:5432/studs";

    public Database(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public static void main(String...args) {
        try {
            Database db = new Database("s452689", "mMUd<5774");
            db.executeUpdate("drop table products");
            db.createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTables() throws SQLException {
        executeUpdate("create table users (id serial primary key, name text, password text)");
        executeUpdate("create table products(id serial primary key," +
                "name text not null," +
                "x double precision not null," +
                "y double precision not null," +
                "creationDate TIMESTAMPTZ not null," +
                "price float4 not null check (price > 0)," +
                "partNumber text null check (length(partNumber) between 23 and 51 or partNumber = null)," +
                "manufactureCost double precision null check (manufactureCost >= 0)," +
                "unitOfMeasure varchar(20) not null check (unitOfMeasure IN ('METERS', 'CENTIMETERS', 'PCS', 'LITERS', 'MILLIGRAMS'))," +
                "personName text," +
                "personHeight float4," +
                "eyeColor varchar(20) not null check (eyeColor IN ('GREEN', 'YELLOW', 'ORANGE', 'BROWN'))," +
                "nationality varchar(20) null check (nationality in ('RUSSIA', 'GERMANY', 'FRANCE', 'SOUTH_KOREA', 'JAPAN', null))," +
                "userId serial references users(id))");
    }

    /**
     * Выполняет запросы на получение продуктов из коллекции.
     *
     * @param query запрос получения продуктов
     * @return список продуктов)))
     */
    public List<Product> executeSelect(String query) throws SQLException {
        try(Connection connection = DriverManager.getConnection(connectionAddress, user, password)) {
            Statement statement = connection.createStatement();
            if (statement.execute(query)) {
                ResultSet result = statement.getResultSet();
                List<Product> products = new ArrayList<>();
                while (result.next()) {
                    products.add(getProductFromQuery(result));
                }
                result.close();
                statement.close();
                connection.close();
                return products;
            } else {
                System.out.println("Wrong query type (use executeUpdate instead)");
            }
        }
        return null;
    }

    /**
     * Добавляет продукт в базу данных. Возвращает id продукта, сгенерированный в базе.
     *
     * @param p продукт
     * @param login логин пользователя, добавляющего продукт
     * @param password пароль пользователя
     * @return id продукта
     * @throws SQLException
     */
    public int addProduct(Product p, String login, String password) throws SQLException {
        int userId = getUserId(login, password);
        Person per = p.getOwner();
        String query = "insert into products (name, x, y, creationDate, price, partNumber, manufactureCost," +
                "unitOfMeasure, personName, personHeight, eyeColor, nationality, userId) values ('" +
                p.getName() +
                "', " + p.getCoordinates().getX()+
                ", " + p.getCoordinates().getY() +
                ", '" + p.getCreationDate() +
                "', " + p.getPrice() +
                ", '" + p.getPartNumber() +
                "', '" + p.getManufactureCost() +
                "', '" + p.getUnitOfMeasure() +
                "', '" + per.getName() +
                "', " + per.getHeight() +
                ", '" + per.getEyeColor() +
                "', '" + per.getNationality() + "'," +
                userId + ")" +
                "returning id";

        try(Connection connection = DriverManager.getConnection(connectionAddress, user, this.password)) {
            Statement statement = connection.createStatement();
            statement.execute(query);
            ResultSet result = statement.getResultSet();
            result.next();

            int id = result.getInt(1);
            result.close();
            statement.close();
            connection.close();
            return id;
        }

    }

    public int removeProductById(int id, String login, String password) throws SQLException {
        int userId = getUserId(login, password);
        return executeUpdate("delete from products where userId =" + userId + " and id = '" + id + "'");
    }

    /**
     * Возврашает объект продукта по строке из базы данных.
     *
     * @param result строка базы данных
     * @return продукт
     * @throws SQLException
     */
    private Product getProductFromQuery(ResultSet result) throws SQLException {
        int id = result.getInt(1);
        String name = result.getString(2);
        double x = result.getDouble(3);
        double y = result.getDouble(4);
        Date date = result.getDate(5);
        Float price = result.getFloat(6);
        String partNumber = result.getString(7);
        Double mC = result.getDouble(8);

        String uOMString = result.getString(9);
        UnitOfMeasure uOM = null;
        for (UnitOfMeasure u : UnitOfMeasure.values()) {
            if (u.toString().equals(uOMString)) uOM = u;
        }

        String ownerName = result.getString(10);
        float ownerHeight = result.getFloat(11);
        String colorString = result.getString(12);
        Color color = null;
        for (Color u : Color.values()) {
            if (u.toString().equals(colorString)) color = u;
        }

        String natString = result.getString(13);
        Country nat = null;
        for (Country u : Country.values()) {
            if (u.toString().equals(natString)) nat = u;
        }

        return new Product(id, name, new Coordinates(x, y), price, partNumber, mC, uOM, new Person(ownerName, ownerHeight, color, nat));
    }

    /**
     *
     *
     * @param user
     * @return
     * @throws SQLException
     */
    public String getUserPassword(String user) throws SQLException{
        try(Connection connection = DriverManager.getConnection(connectionAddress, this.user, password)) {
            PreparedStatement statement = connection.prepareStatement("select password from users where name = ?");
            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                throw new UserNotFound(user);
            }
        }
    }

    public int getUserId(String user, String password) throws SQLException {
        int resp;
        try(Connection connection = DriverManager.getConnection(connectionAddress, this.user, this.password)) {
            PreparedStatement statement = connection.prepareStatement("select id from users where name = ? and password = ?");
            statement.setString(1, user);
            statement.setString(2, password);
            statement.execute();
            ResultSet result = statement.getResultSet();
            if (result.next()) {
                resp = result.getInt(1);
            } else {
                throw new UserNotFound(user);
            }
            result.close();
            statement.close();
            connection.close();
            return resp;
        }
    }


    public boolean register(String login, String password) throws SQLException {
        int i = executeUpdate("insert into users (name, password) values ('" + login +"', '"+ password + "')");
        return (i==1);
    }


    /**
     * Выполняет запрос, связанный с изменением таблицы базы данных.
     *
     * @param query текст запроса
     * @return количество изменённых строк
     */
    public int executeUpdate(String query) throws SQLException {
        try(Connection connection = DriverManager.getConnection(connectionAddress, user, password)) {
            Statement statement = connection.createStatement();
            statement.execute(query);
            int count = statement.getUpdateCount();
            statement.close();
            connection.close();
            return count;
        }
    }
}
