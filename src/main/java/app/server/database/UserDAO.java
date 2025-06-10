package app.server.database;

import app.product.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends CrudRepository<UserEntity, Integer> {
    List<UserEntity> findByNameAndPassword(String name, String password);
    Optional<UserEntity> findByName(String name);
}
