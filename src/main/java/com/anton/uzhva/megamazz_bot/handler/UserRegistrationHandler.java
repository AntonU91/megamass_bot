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
    public UserRegistrationHandler(TelegramService telegramService, UserSessionService userSessionService, UserService userService, KeyboardHelper keyboardHelper) {
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
        this.userService = userService;
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
        User user = createAndSaveUser(userSession, userLogin);
        userSession.setUser(user);
        telegramService.sendMessage(userSession.getChatId(),
                String.format("Hi,%s. Choose what you want me to do", userLogin), keyboardHelper.mainMenu());
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        userSessionService.saveUserSession(request.getChatId(), userSession);
    }

    private User createAndSaveUser(UserSession userSession, String userLogin) {
        User user = new User(); /// todo find out how to implement Builder and solve the problem with User.class
        user.setUserLogin(userLogin);
        user.setDefaultExercises();
        user.setId(userSession.getChatId());
        userService.saveUser(user);
        return user;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
