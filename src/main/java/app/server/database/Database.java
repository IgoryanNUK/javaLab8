package app.server.database;

import app.product.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private String user;
    private String password;

    public Database(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public static void main(String...args) {
        try {
            Database db = new Database("s452689", "mMUd<5774");
            db.executeUpdate("drop table huy");
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
                "partNumber text null check (length(partNumber) between 23 and 51)," +
                "manufactureCost double precision unique null check (manufactureCost > 0)," +
                "unitOfMeasure varchar(20) not null check (unitOfMeasure IN ('METERS', 'CENTIMETERS', 'PCS', 'LITERS', 'MILLIGRAMS'))," +
                "personName text," +
                "personHeight float4," +
                "eyeColor varchar(20) not null check (eyeColor IN ('GREEN', 'YELLOW', 'ORANGE', 'BROWN'))," +
                "nationality varchar(20) null check (nationality in ('RUSSIA', 'GERMANY', 'FRANCE', 'SOUTH_KOREA', 'JAPAN'))," +
                "userId serial references users(id))");
    }

    /**
     * Выполняет запросы на получение продуктов из коллекции.
     *
     * @param query запрос получения продуктов
     * @return список продуктов)))
     */
    public List<Product> executeSelect(String query) throws SQLException {
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", user, password)) {
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
     * Возврашает объект продукта по строке из базы данных.
     *
     * @param result строка базы данных
     * @return продукт
     * @throws SQLException
     */
    private Product getProductFromQuery(ResultSet result) throws SQLException {
        int id = result.getInt(0);
        String name = result.getString(1);
        double x = result.getDouble(2);
        double y = result.getDouble(3);
        Date date = result.getDate(4);
        Float price = result.getFloat(5);
        String partNumber = result.getString(6);
        Double mC = result.getDouble(7);

        String uOMString = result.getString(8);
        UnitOfMeasure uOM = null;
        for (UnitOfMeasure u : UnitOfMeasure.values()) {
            if (u.toString().equals(uOMString)) uOM = u;
        }

        String ownerName = result.getString(9);
        float ownerHeight = result.getFloat(10);
        String colorString = result.getString(11);
        Color color = null;
        for (Color u : Color.values()) {
            if (u.toString().equals(colorString)) color = u;
        }

        String natString = result.getString(8);
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
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", user, password)) {
            PreparedStatement statement = connection.prepareStatement("select password from users where name = ?");
            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(0);
            } else {
                throw new UserNotFound(user);
            }
        }
    }


    /**
     * Выполняет запрос, связанный с изменением таблицы базы данных.
     *
     * @param query текст запроса
     * @return количество изменённых строк
     */
    public int executeUpdate(String query) throws SQLException {
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", user, password)) {
            Statement statement = connection.createStatement();
            statement.execute(query);
            int count = statement.getUpdateCount();
            statement.close();
            connection.close();
            return count;
        }
    }
}
