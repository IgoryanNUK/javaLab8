package app.exceptions;

public class CorruptedFile extends KnownException {
    private String fileName;

    public CorruptedFile(String fileName) {
        super("");
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        return "Файл " + fileName + " повреждён.";
    }
}
