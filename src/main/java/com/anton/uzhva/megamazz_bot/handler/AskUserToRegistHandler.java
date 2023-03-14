package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AskUserToRegistHandler {
    TelegramService telegramService;

    @Autowired
    public AskUserToRegistHandler(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public void handle(UserRequest request) {
        telegramService.sendMessage(request.getChatId(), "Firstly, you should log in! Please, make up your login and input it");
    }

}
