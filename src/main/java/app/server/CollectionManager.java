package app.server;

import app.exceptions.UnknownException;
import app.product.Product;
import app.server.database.Database;
import app.server.database.UserNotFound;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

public class CollectionManager {
    private CopyOnWriteArraySet<Product> products;
    private final Database database;

    public CollectionManager(String dbUser, String dbPassword) throws SQLException {
        database = new Database(dbUser, dbPassword);

        products = new CopyOnWriteArraySet<>(load());
    }

    private List<Product> load() throws SQLException {
        try {
            return database.executeSelect("select * from products");
        } catch(SQLException s) {
            if (s.getMessage().startsWith("ОШИБКА: отношение \"products\" не существует")) {
                database.createTables();
                return new ArrayList<>();
            } else {
                throw s;
            }
        }
    }

    public boolean register(String login, String password) throws SQLException {
        String hp;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            hp = new String(md.digest(password.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new UnknownException(e);
        }

        try {
            return hp.equals(database.getUserPassword(login));
        } catch(UserNotFound u) {
            return database.register(login, hp);
        }
    }

    public boolean auth(String login, String password) {
        boolean resp;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String hp = new String(md.digest(password.getBytes("UTF-8")));
            if (hp.equals(database.getUserPassword(login))) {
                resp = true;
            } else {
                resp = false;
            }
        } catch (UserNotFound u) {
            resp = false;
        } catch (Exception e) {
            throw new UnknownException(e);
        }

        return resp;
    }

    public boolean add(String login, String password, Product ... ps) {
        try {
            int total = 0;
            for (Product p : ps) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                String hp = new String(md.digest(password.getBytes("UTF-8")));
                int i = database.addProduct(p, login, hp);
                if (i == 1) {
                    total += i;
                    products.add(p);
                }
            }

            return total == ps.length;
        } catch (Exception e) {throw new UnknownException(e);}
    }

    /** Возвращает тип коллекции. */
    public String getCollectionName() {
        return products.getClass().getName();
    }

    public int getSize() {return products.size();}

    /**
     * Удаляет все элементы, удовлетворяющие заданному условию.
     *
     * @param p условие, по которому удаляются объекты
     * @return true, если хотя бы один элемент был удалён из коллекции.
     */
    public boolean removeIf(Predicate<Product> p){




        boolean b = products.removeIf(p);
        //не забыть про изменения!!!
        return b;
    }

    /**
     * Возвращает все элементы, удовлетворяющие заданному условию.
     *
     * @param p условие
     * @return массив продуктов
     */
    public List<Product> getIf(Predicate<Product> p) {
        List<Product> array = new ArrayList<>();
        products.stream().filter(p).forEach(array::add);
        return array.stream().toList();
    }
}
