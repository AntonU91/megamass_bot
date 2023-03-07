package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.User;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.anton.uzhva.megamazz_bot.constant.Constants.*;

@Component
public class UserRegistrationHandler extends UserRequestHandler {
    TelegramService telegramService;
    UserSession userSession;
    UserSessionService userSessionService;
    UserService userService;
    KeyboardHelper keyboardHelper;

    @Autowired
    public UserRegistrationHandler(TelegramService telegramService, UserSession userSession, UserSessionService userSessionService, UserService userService, KeyboardHelper keyboardHelper) {
        this.telegramService = telegramService;
        this.userSession = userSession;
        this.userSessionService = userSessionService;
        this.keyboardHelper= keyboardHelper;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isValidTextMessage(request.getUpdate(), REGEX_USER_LOGIN)
                && request.getSession().getState().equals(ConversationState.USER_REGISTRATION);
    }

    @Override
    public void handle(UserRequest request) {
        String userLogin = request.getUpdate().getMessage().getText();
        userSession = userSessionService.getSession(request.getChatId());
        createAndSaveUser(userSession, userLogin);
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        telegramService.sendMessage(userSession.getChatId(),
                String.format("Привет, %s", userLogin), keyboardHelper.mainMenu());
        userSessionService.saveUserSession(request.getChatId() ,userSession);
    }

    private void createAndSaveUser(UserSession userSession, String userLogin) {
        User user = userSession.getUser();
        user.setUserLogin(userLogin);
        user.setDefaultExercises();
        user.setId(userSession.getChatId());
        userSession.setUser(user);
        userService.saveUser(user);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
