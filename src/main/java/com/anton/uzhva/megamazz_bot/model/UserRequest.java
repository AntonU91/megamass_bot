package com.anton.uzhva.megamazz_bot.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.Update;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserRequest {
    Update update;
    long chatId;
    UserSession session;
}
