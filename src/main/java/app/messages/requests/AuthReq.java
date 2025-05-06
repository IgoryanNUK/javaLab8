package app.messages.requests;

import app.client.UserIOManager;
import app.messages.commands.Command;

public class AuthReq extends Request {
    private RequestType type = RequestType.AUTH;

    public AuthReq(String login, String password) {
        setLogin(login);
        setPassword(password);
    }

    @Override
    public RequestType getType() {return type;}
}
