package com.anton.uzhva.megamazz_bot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class BotConfig {
    @Value("${bot.name}")
    String botUserName;
    @Value("${bot.token}")
    String botToken;

}
