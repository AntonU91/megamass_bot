package com.anton.uzhva.megamazz_bot.bot;

import com.anton.uzhva.megamazz_bot.Dispatcher;
import com.anton.uzhva.megamazz_bot.config.BotConfig;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewMegamassBot extends TelegramLongPollingBot {
    BotConfig botConfig;
    UserSessionService userSessionService;
    Dispatcher dispatcher;

    @Autowired
    public NewMegamassBot(BotConfig botConfig, UserSessionService userSessionService, Dispatcher dispatcher) {
        this.botConfig = botConfig;
        this.userSessionService = userSessionService;
        this.dispatcher = dispatcher;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            UserSession userSession = userSessionService.getSession(chatId);
            UserRequest userRequest = getUserRequest(update, chatId, userSession);
            dispatcher.dispatch(userRequest);
        } else if (update.hasCallbackQuery()) {
            UserSession userSession = userSessionService.getSession(chatId);
            UserRequest userRequest = getUserRequest(update, chatId, userSession);
            dispatcher.dispatchCallBack(userRequest);
        }
    }

    private UserRequest getUserRequest(Update update, long chatId, UserSession userSession) {
        return UserRequest.builder()
                .chatId(chatId)
                .session(userSession)
                .update(update)
                .build();
    }
}
