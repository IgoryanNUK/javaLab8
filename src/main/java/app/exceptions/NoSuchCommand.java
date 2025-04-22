package app.exceptions;

public class NoSuchCommand extends KnownException {
    private String commandName;

    public NoSuchCommand(String message) {
        super(message);
      commandName = message;
    }

    @Override
    public String getMessage() {
      return "Команды \"" + commandName + "\" не существует.";
    }
}
