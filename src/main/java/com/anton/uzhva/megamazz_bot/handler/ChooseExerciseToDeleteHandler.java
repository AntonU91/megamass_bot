package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChooseExerciseToDeleteHandler extends UserRequestHandler {
    UserService userService;
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;

    @Autowired
    public ChooseExerciseToDeleteHandler(UserService userService, UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper) {
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return request.getSession().getState().equals(ConversationState.CHOOSING_EXERCISE)
                && isValidTextMessage(request.getUpdate(), Constants.DELETE_EXERCISE);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        telegramService.sendMessage(request.getChatId(), "Choose the exercise to delete",
                keyboardHelper.listOfExercisesToDelete(request.getChatId()));
        userSession.setState(ConversationState.DELETING_EXERCISE);
        userSessionService.saveUserSession(request.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
