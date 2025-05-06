package app.messages.commands;

import app.client.Client;
import app.client.UserIOManager;
import app.messages.requests.RegistrationReq;
import app.messages.requests.Request;

public class Register extends Command {
    {
        name = "register";
        description = "Регистрация нового пользователя.";
    }
    private Client app;

    public Register(Client app) {
        this.app = app;
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager io) {
        String login = io.ask("Введите логин: ");
        String pswd = io.ask("Введите пароль: ");
        if (pswd.equals(io.ask("Подтвердите пароль: "))) {
            app.setLoginAndPassword(login, pswd);
            return new RegistrationReq(login, pswd);
        } else {
            io.println("Пароли не совпадают( Пожалуйста, повторите регистрацию.");
            return null;
        }
    }
}
