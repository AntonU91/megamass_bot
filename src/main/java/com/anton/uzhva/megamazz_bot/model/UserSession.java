package com.anton.uzhva.megamazz_bot.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSession {
     Long chatId;
     ConversationState state;
     Exercise exercise;
     User user;
}
