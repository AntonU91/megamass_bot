package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.handler.UserCallBackRequestHandler;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllResultsHandlers implements UserCallBackRequestHandler {
    ExerciseService exerciseService;
    TelegramService telegramService;
    UserSessionService userSessionService;
    KeyboardHelper keyboardHelper;

    @Autowired
    public DeleteAllResultsHandlers(ExerciseService exerciseService, TelegramService telegramService, UserSessionService userSessionService, KeyboardHelper keyboardHelper) {
        this.exerciseService = exerciseService;
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        exerciseService.deleteAllUserTrainingsResults(userRequest.getChatId());
        telegramService.sendMessage(userRequest.getChatId(), " All training results was deleted.\nMoving on!",
                keyboardHelper.mainMenu());
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        userSessionService.saveUserSession(userRequest.getChatId(), userSession);
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.WAITING_FOR_REQUEST)
                && isValidCallBack(userRequest.getUpdate(), Constants.DELETE_ALL_RESULTS);
    }
}
