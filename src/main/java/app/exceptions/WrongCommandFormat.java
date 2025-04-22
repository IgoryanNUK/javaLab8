package app.exceptions;

public class WrongCommandFormat extends KnownException {
    private String commandName;

    public WrongCommandFormat(String commandName) {
        super("");
        this.commandName = commandName;
    }

    @Override
    public String getMessage() {
        return "Неверный формат команды " + commandName + ".";
    }
}
