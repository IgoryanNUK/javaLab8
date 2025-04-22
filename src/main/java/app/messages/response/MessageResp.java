package app.messages.response;

/**
 * Ответ сервера, содержащий текстовое сообщение.
 */
public class MessageResp implements Response {
    private final ResponseType type = ResponseType.MESSAGE;
    private final String message;

    public MessageResp(String message) {
        this.message= message;
    }

    @Override
    public ResponseType getType() {return type;}

    public String getMessage() {return message;}
}
