package app.messages.response;

import java.io.Serializable;

public interface Response extends Serializable {
    ResponseType getType();
}
