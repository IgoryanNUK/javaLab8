package app.messages.requests;


public class AddReq extends Request {
    private final RequestType type;
    private final String jsonMessage;

    public AddReq(RequestType type, String jsonMessage) {
        this.jsonMessage = jsonMessage;
        this.type = type;
    }

    @Override
    public RequestType getType() {return type;}

    public String getJsonMessage() {return jsonMessage;}
}
