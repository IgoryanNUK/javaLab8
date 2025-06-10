package app.messages.commands;

import app.client.Client;
import app.client.UserIOManager;
import app.messages.requests.RegistrationReq;
import app.messages.requests.Request;

import java.security.MessageDigest;

public class Register extends Command {
    {
        name = "register";
        description = "Регистрация нового пользователя.";
    }
    private final Client app;

    public Register(Client app) {
        this.app = app;
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager io) {
        String login = io.ask("Введите логин: ");

        String pswd;
        while (true) {
            pswd = io.ask("Введите пароль: ");

            if (pswd.contains("'") | pswd.contains(";") | pswd.contains("--")) {
                System.out.println("В пароле обнаружены запрещённые символы (' ; --)");
            } else {
                break;
            }
        }

        if (pswd.equals(io.ask("Подтвердите пароль: "))) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                pswd = new String(md.digest(pswd.getBytes("UTF-8")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            app.setLoginAndPassword(login, pswd);

            return new RegistrationReq(login, pswd);
        } else {
            io.println("Пароли не совпадают( Пожалуйста, повторите регистрацию.");
            return null;
        }
    }
}
