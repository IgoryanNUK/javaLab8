package app.exceptions;

public class UnavaluableServer extends KnownException {
    public UnavaluableServer() {
        super("");
    }

    @Override
    public String getMessage() {
      return "Сервер сейчас недоступен. Повторите попытку позже.";
    }
}
