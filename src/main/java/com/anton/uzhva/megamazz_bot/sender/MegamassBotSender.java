package com.anton.uzhva.megamazz_bot.sender;

import com.anton.uzhva.megamazz_bot.config.BotConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MegamassBotSender  extends DefaultAbsSender {
    final BotConfig botConfig;

    @Autowired
    public MegamassBotSender(BotConfig botConfig) {
        super(new DefaultBotOptions());
        this.botConfig = botConfig;
    }

    @Value("${bot.token}")
     String botToken;

    @Getter
    @Value("${bot.name}")
    String botName;

    @Override
    public String getBotToken() {
     return botConfig.getBotToken();
    }
}
