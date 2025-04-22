package app.messages.commands;

import app.client.UserIOManager;
import app.messages.requests.Request;

import java.io.Serializable;

public interface ICommand extends Serializable {
    String getName();
    String getDescription();
    Request prepareRequest(String[] args, UserIOManager ioManager);
}
