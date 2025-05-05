package app.server.database;

import app.exceptions.KnownException;

public class UserNotFound extends KnownException {
    private final String userName;

    public UserNotFound(String user) {
        super("");
        userName = user;
    }

    @Override
    public String getMessage() {return "Не найдено пользователя с именем \"" + userName + "\".";}
}
