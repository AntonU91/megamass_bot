package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;

import static com.anton.uzhva.megamazz_bot.constant.Constants.*;

@Component
public class StartCommandHandler extends UserRequestHandler {
    TelegramService telegramService;
    UserSession userSession;
    UserSessionService userSessionService;
    UserService userService;
    UserRegistrationHandler userRegistrationHandler;
    KeyboardHelper keyboardHelper;

    @Autowired
    public StartCommandHandler(TelegramService telegramService, UserSessionService userSessionService,
                               UserService userService, UserRegistrationHandler userRegistrationHandler, KeyboardHelper keyboardHelper) {
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.userService = userService;
        this.userRegistrationHandler = userRegistrationHandler;
        this.keyboardHelper = keyboardHelper;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isCommand(request.getUpdate(), START_COMMAND);
    }

    @Override
    public void handle(UserRequest request) {
        userSession = userSessionService.getSession(request.getChatId());
        if (isUserRegistered(request.getChatId())) {
            userService.getUserLogin(request.getChatId());
            telegramService.sendMessage(userSession.getChatId(),
                    String.format("Hi,%s. Choose what you want me to do", userService.getUserLogin(request.getChatId())), keyboardHelper.mainMenu());
            userSession.setState(ConversationState.WAITING_FOR_REQUEST);
            userSessionService.saveUserSession(request.getChatId(), userSession);
        } else {
            String userName = request.getUpdate()
                    .getMessage()
                    .getFrom()
                    .getFirstName();
            userSession.setState(ConversationState.USER_REGISTRATION);
            telegramService.sendMessage(userSession.getChatId(),
                    String.format("Hi,%s.To continue using bot make your login up and enter this one", userName));
            userSessionService.saveUserSession(request.getChatId(), userSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    private boolean isUserRegistered(long chatId) {
        try {
            userService.getUserLogin(chatId);
        } catch (NoResultException ex) {
            return false;
        }
        return true;
    }
}
