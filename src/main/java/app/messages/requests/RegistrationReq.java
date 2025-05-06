package app.messages.requests;

public class RegistrationReq extends Request {
    private RequestType type = RequestType.REG;

    public RegistrationReq(String login, String password) {
        setLogin(login);
        setPassword(password);
    }

    @Override
    public RequestType getType() {return type;}
}
