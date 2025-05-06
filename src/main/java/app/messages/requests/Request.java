package app.messages.requests;

public abstract class Request implements IRequest {
    private String login;
    private String password;

    @Override
    public void setLogin(String login) {this.login = login;}

    @Override
    public void setPassword(String pswd) {this.password = pswd;}

    public String getLogin() {return login;}

    public String getPassword() {return password;}
}
