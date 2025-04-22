package app.exceptions;

public class UnknownException extends RuntimeException {
    Throwable exception;

    public UnknownException(Throwable e) {
        super("");
        exception = e;
    }

    @Override
    public String getMessage() {
        return "Произошла неизвестная ошибка: " + exception.getMessage();
    }
}
