package app.server.database;

import app.product.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    public static void main(String...args) {
        try {
            DatabaseConnection db = new DatabaseConnection();
            int r = db.executeUpdate("insert into products (id, name, x, y) values (1, 'Fat', 4, 5)");
            System.out.println(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createStudentsTable() {
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", "postgres", "25311357Sql")) {
            Statement statement = connection.createStatement();
            if (statement.execute("create table products(id integer primary key check (id > 0)," +
                    "name text not null," +
                    "x double precision not null," +
                    "y double precision not null," +
                    "creationDate date not null," +
                    "price float4 not null check (price > 0)," +
                    "partNumber text null check (length(partNumber) between 23 and 51)," +
                    "manufactureCost double precision unique null check (manufactureCost > 0)," +
                    "unitOfMeasure varchar(20) not null check (unitOfMeasure IN ('METERS', 'CENTIMETERS', 'PCS', 'LITERS', 'MILLIGRAMS'))," +
                    "personId integer)")) {
                ResultSet result = statement.getResultSet();
                while (result.next()) {
                    System.out.println("name: " + result.getString(2));
                }
                result.close();
            } else {
                System.out.println(statement.getUpdateCount());
            }
            statement.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private String selectAll(String tableName) {
        return null;
    }

    private List<Product> executeSelect(String query) {
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", "postgres", "25311357Sql")) {
            Statement statement = connection.createStatement();
            if (statement.execute(query)) {
                ResultSet result = statement.getResultSet();
                List<Product> products = new ArrayList<>();
                while (result.next()) {
                    products.add(getProductFromQuery(result));
                }

            } else {
                System.out.println("Wrong query type (use executeUpdate instead)");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Product getProductFromQuery(ResultSet result) {

    }

    private int executeUpdate(String query) {
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/studs", "postgres", "25311357Sql")) {
            Statement statement = connection.createStatement();
            statement.execute(query);
            connection.close();
            return statement.getUpdateCount();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }







}
