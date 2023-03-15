package com.anton.uzhva.megamazz_bot.handler.commands_handler;

import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.model.UserRequest;

public class ChangeLoginHandler  extends UserRequestHandler {
    @Override
    public boolean isApplicable(UserRequest request) {
        return false;
    }

    @Override
    public void handle(UserRequest request) {

    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
