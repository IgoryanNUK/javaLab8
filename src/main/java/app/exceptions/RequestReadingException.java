package app.exceptions;

import app.messages.requests.RequestType;

public class RequestReadingException extends KnownException {
    private final RequestType type;
    private final Throwable exception;

    public RequestReadingException(RequestType type, Throwable e) {
        super("");
        this.type = type;
        exception = e;
    }

    @Override
    public String getMessage() {
        return "Возникла ошибка при чтении запроса " + type + ":\n" +
                exception.getMessage();
    }
}
