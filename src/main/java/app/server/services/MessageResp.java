package app.server.services;

/**
 * Ответ сервера, содержащий текстовое сообщение.
 */
public class MessageResp {
    private final String message;

    public MessageResp(String message) {
        this.message= message;
    }

    public String getMessage() {return message;}
}
