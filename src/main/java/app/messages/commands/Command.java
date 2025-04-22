package app.messages.commands;

import java.io.Serializable;

public abstract class Command implements ICommand, Serializable {
    protected String name = "command";
    protected String description = "";

    @Override
    public String getName() {return name;}

    @Override
    public String getDescription() {return description;}
}

//id запроса и ответа

//разделить тип команд
//юзать рекорд
//максимально простые данные между сервером и клиентом

//1 максимально
//2 минимизировать трафик