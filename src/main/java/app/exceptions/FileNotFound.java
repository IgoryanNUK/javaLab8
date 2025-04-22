package app.exceptions;

public class FileNotFound extends KnownException {
    private final String filePath;
    private final String purpose;

    public FileNotFound(String purpose, String filePath) {
        super("");
        this.filePath = filePath;
        this.purpose = purpose;
    }

    @Override
    public String getMessage() {
        return "Не найден файл на " + purpose + ": " + filePath;
    }
}
