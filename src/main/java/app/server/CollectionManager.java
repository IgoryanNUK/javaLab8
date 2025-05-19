package app.server;

import app.exceptions.UnknownException;
import app.product.Product;
import app.server.database.Database;
import app.server.database.UserNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

@Component
public class CollectionManager {
    private final CopyOnWriteArraySet<Product> products;
    private final Database database;

    @Autowired
    public CollectionManager(Database database) throws SQLException {
        this.database = database;
        products = new CopyOnWriteArraySet<>(load());
    }

    /**
     * Возвращает список прочитанных из бд продуктов.
     *
     * @return список продуктов
     * @throws SQLException
     */
    private List<Product> load() throws SQLException {
        try {
            return database.executeSelect("select * from products");
        } catch(SQLException s) {
            if (s.getMessage().startsWith("ОШИБКА: отношение \"products\" не существует") | s.getMessage().startsWith("ERROR: relation \"products\" does not exist")) {
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
            for (Product p : ps) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                String hp = new String(md.digest(password.getBytes("UTF-8")));
                int id = database.addProduct(p, login, hp);
                p.setId(id);
                products.add(p);

            }

            return true;
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
    public boolean removeIf(String login, String password, Predicate<Product> p){
        try {
            int total = 0;

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String hp = new String(md.digest(password.getBytes("UTF-8")));

            List<Product> cand = getIf(p);
            for (Product pr : cand) {
                int i = database.removeProductById(pr.getId(), login, hp);
                if (i==1) {
                    total += i;
                    products.removeIf(e -> e.getName().equals(pr.getName()));
                }
            }

            return total == cand.size();
        } catch (Exception e) {
            throw new UnknownException(e);
        }
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
