package app.messages.response;

public class AccessResp implements Response{
    private final ResponseType type = ResponseType.ACCESS;
    private final String login;
    private final String password;

    public AccessResp(String login, String password) {
        this.login= login;
        this.password = password;
    }

    @Override
    public ResponseType getType() {return type;}

    public String getLogin() {return login;}

    public String getPassword() {return password;}
}
