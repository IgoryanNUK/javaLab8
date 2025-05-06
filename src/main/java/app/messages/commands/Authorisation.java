package app.messages.commands;

import app.client.UserIOManager;
import app.messages.requests.AuthReq;
import app.messages.requests.RegistrationReq;
import app.messages.requests.Request;

public class Authorisation extends Command {
    {
        name = "authorisation";
        description = "Авторизация";
    }

    @Override
    public Request prepareRequest(String[] args, UserIOManager io) {
        String login = io.ask("Введите логин: ");
        String pswd = io.ask("Введите пароль: ");

        return new AuthReq(login, pswd);
    }
}
