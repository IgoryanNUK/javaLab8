package app.exceptions;

public class RecursiveCallError extends KnownException {
    public RecursiveCallError(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Произошёл рекурсивный вывод. Не делайте так, пожалуйста, в следующий раз солью IP.";
    }
}
