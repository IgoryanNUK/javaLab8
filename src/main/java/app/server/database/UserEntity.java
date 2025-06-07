package app.server.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue
    private int id;
    private final String name;
    private final String password;

    public UserEntity(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public int getId() {return id;}

    public String getPassword() {return password;}
}
