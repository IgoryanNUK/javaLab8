package app.server.database;

import app.exceptions.UnknownException;
import app.product.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class Database {
    private final CrudRepository<Product, Integer> productDao;
    private final UserDAO userDao;

    public Database(@Autowired CrudRepository<Product, Integer> pDAO,
                    @Autowired UserDAO uDAO) {
        this.userDao = uDAO;
        this.productDao = pDAO;
    }

    public static void main(String...args) {

    }


    public void createTables() throws SQLException {
        executeUpdate("create table users (id serial primary key, name text, password text)");
        executeUpdate("create table products(id serial primary key," +
                "name text not null," +
                "x double precision not null," +
                "y double precision not null," +
                "creationDate TIMESTAMPTZ not null," +
                "price float4 not null check (price > 0)," +
                "partNumber text null unique check(length(partNumber) between 23 and 51 or partNumber = null)," +
                "manufactureCost double precision null check (manufactureCost >= 0)," +
                "unitOfMeasure varchar(20) not null check (unitOfMeasure IN ('METERS', 'CENTIMETERS', 'PCS', 'LITERS', 'MILLIGRAMS'))," +
                "personName text," +
                "personHeight float4," +
                "eyeColor varchar(20) not null check (eyeColor IN ('GREEN', 'YELLOW', 'ORANGE', 'BROWN'))," +
                "nationality varchar(20) null check (nationality in ('RUSSIA', 'GERMANY', 'FRANCE', 'SOUTH_KOREA', 'JAPAN', null))," +
                "userId serial references users(id))");
    }

    /**
     * @return крличество продуктов в базе данных.
     */
    public long getProductsCount() {
        return productDao.count();
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
    @Transactional
    public boolean addProduct(Product p, String login, String password) {
        boolean ans;

        try {
            int userId = getUserId(login, password);
            p.setUserId(userId);
            productDao.save(p);
            ans = true;
        } catch (UserNotFound u) {
            ans = false;
        }

        return ans;
    }

    @Transactional
    public List<Product> getIf(Predicate<Product> pred) {
        Iterable<Product> collection = productDao.findAll();
        Iterator<Product> it = collection.iterator();
        ArrayList<Product> ans = new ArrayList<>();

        while (it.hasNext()) {
            Product p = it.next();
            if (pred.test(p)) {
                ans.add(p);
            }
        }

        return ans;
    }

    /**
     * Удаляет продукт из базы по предикату.
     * Может удалить только те продукты, которые принадлежат пользователю с login и password.
     *
     * @param pred предикат
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return true, если удален хотя бы 1 элемент
     */
    @Transactional
    public boolean removeIf(Predicate<Product> pred, String login, String password) {
        int userId = getUserId(login, password);
        Iterable<Product> collection = productDao.findAll();
        Iterator<Product> it = collection.iterator();

        boolean ans = false;
        while (it.hasNext()) {
            Product p = it.next();
            if (pred.test(p)) {
                if (p.getUserId() == userId) {
                    productDao.delete(p);
                    ans = true;
                }
            }
        }

        return ans;
    }


    /**
     * Обновляет продукт.
     * Возвращает 1, если продукт успешно обновился, 0 - не нашёл продукта с указанным id, -1 - найденный продукт принадлежит другому пользователю.
     *
     * @param p продукт
     * @param login логин
     * @param password пароль
     * @return статус операции
     */
    @Transactional
    public int updateProduct(Product p, String login, String password) {
        int userId = getUserId(login, password);
        p.setUserId(userId);
        int ans;

        Optional<Product> opt = productDao.findById(p.getId());
        if (opt.isPresent()) {
            Product original = opt.get();
            if (original.getUserId() == userId) {
                productDao.save(p);
                ans = 1;
            } else {
                ans = -1; //отказано в доступе
            }
        } else {
            ans = 0; //не нашёл элемента по id
        }

        return ans;
    }

    /**
     * Находит id пользователя. Проверяет, что пароль пользователя совпадает с паролем в базе.
     *
     * @param user логин
     * @param password пароль
     * @return id пользователя
     * @throws SQLException
     */
    @Transactional
    public int getUserId(String user, String password) {
        int resp;

        List<UserEntity> list = userDao.findByNameAndPassword(user, password);
        if (list.isEmpty()) {
            throw new UserNotFound(user);
        } else {
            resp = list.get(0).getId();
        }

        return resp;
    }

    /**
     * Регистрирует пользователя. Заносит логин и пароль пользователя без дополнительных проверок.
     *
     * @param login логин
     * @param password пароль
     * @return прошло ли изменение успешно
     * @throws SQLException
     */
    @Transactional
    public boolean register(String login, String password) throws SQLException {
        Optional<UserEntity> opt = userDao.findByName(login);
        boolean res;
        if (opt.isEmpty()) {
            userDao.save(new UserEntity(login, password));
            res = true;
        } else {
            res = false;
        }

        return res;
    }


    public boolean auth(String login, String password) {
        boolean resp;

        Optional<UserEntity> opt = userDao.findByName(login);
        if (opt.isEmpty()) {
            resp = false;
        } else {
            UserEntity user = opt.get();
            resp = user.getPassword().equals(password);
        }

        return resp;
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
