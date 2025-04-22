package app.exceptions;

public class UnexceptableValue extends KnownException {
    private String fieldName;

    public UnexceptableValue(String fieldName) {
        super("");
        this.fieldName = fieldName;
    }

    @Override
    public String getMessage() {
        return "Неприемлимое значение для поля " + fieldName;
    }
}
