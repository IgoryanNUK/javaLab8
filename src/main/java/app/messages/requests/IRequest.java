package app.messages.requests;

import java.io.Serializable;

public interface IRequest extends Serializable {
    RequestType getType();
    void setLogin(String login);
    void setPassword(String pswd);
}
