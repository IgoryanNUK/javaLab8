package app.messages.requests;

public class InfoReq extends Request {
    private RequestType type = RequestType.INFO;

    @Override
    public RequestType getType() {return type;}
}
