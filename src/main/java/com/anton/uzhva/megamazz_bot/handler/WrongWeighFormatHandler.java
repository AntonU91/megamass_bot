package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WrongWeighFormatHandler {
    TelegramService telegramService;

    public void handle(UserRequest userRequest) {
        telegramService.sendMessage(userRequest.getChatId(), "⛔You have typed wrong format data!Try again. Use only digit.\n" +
                " You can use decimal number format, e.g 56.8");
    }
}
