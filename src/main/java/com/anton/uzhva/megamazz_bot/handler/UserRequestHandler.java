package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.model.UserRequest;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class UserRequestHandler {
    public abstract boolean isApplicable(UserRequest request);
    public abstract void handle(UserRequest dispatchRequest);
    public abstract boolean isGlobal();

    public boolean isCommand(Update update, String command) {
        return update.hasMessage() && update.getMessage().isCommand()
                && update.getMessage().getText().equals(command);
    }

    public boolean isValidTextMessage(Update update, String regex) {
        return update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().matches(regex);
    }

}
