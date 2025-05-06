package app.server;

import app.product.Product;
import app.server.database.Database;

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
        return database.register(login, password);
    }

    public void add(Product ... ps) {
        //изменения!!!
        products.addAll(Arrays.asList(ps));
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
