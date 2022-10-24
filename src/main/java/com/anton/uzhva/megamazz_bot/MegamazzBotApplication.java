package com.anton.uzhva.megamazz_bot;

import com.anton.uzhva.megamazz_bot.bot.MegamazzBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class MegamazzBotApplication {

    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(MegamazzBotApplication.class, args);
    }

}
