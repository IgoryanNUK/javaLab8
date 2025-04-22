package app.exceptions;

public abstract class KnownException extends RuntimeException {
    public KnownException(String message) {
        super(message);
    }

    @Override
    public abstract String getMessage();
}
