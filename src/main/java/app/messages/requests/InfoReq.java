package app.messages.requests;

public class InfoReq implements Request {
    private RequestType type = RequestType.INFO;

    @Override
    public RequestType getType() {return type;}
}
