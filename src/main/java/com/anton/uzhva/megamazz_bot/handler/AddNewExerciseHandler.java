package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddNewExerciseHandler extends UserRequestHandler {
    UserService userService;
    UserSessionService userSessionService;
    TelegramService telegramService;

    @Autowired
    public AddNewExerciseHandler(UserService userService, UserSessionService userSessionService, TelegramService telegramService) {
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return request.getSession().getState().equals(ConversationState.CHOOSING_EXERCISE)
                && isValidTextMessage(request.getUpdate(), Constants.ADD_NEW_EXERCISE);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        telegramService.sendMessage(request.getChatId(), "Input name of new exercise ✍️");
        userSession.setState(ConversationState.CREATING_NEW_EXERCISE);
        userSessionService.saveUserSession(request.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
