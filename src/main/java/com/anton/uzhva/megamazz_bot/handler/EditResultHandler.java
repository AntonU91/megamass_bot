package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EditResultHandler implements UserCallBackRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    ExerciseService exerciseService;

    @Autowired
    public EditResultHandler(UserSessionService userSessionService, TelegramService telegramService, ExerciseService exerciseService) {
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
        this.exerciseService = exerciseService;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        Exercise exercise = (Exercise) exerciseService.findExerciseByRecordDate(userSession.getExercise().getRecordDate());
        telegramService.sendMessage(userRequest.getChatId(), String.format("To edit  exercise \"%s\" result, input  once again weight value",
                exercise.getName()));
        userSession.setState(ConversationState.INPUTTING_WEIGHT);
        userSessionService.saveUserSession(userRequest.getChatId(), userSession);
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.WAITING_FOR_REQUEST)
                && isValidCallBack(userRequest.getUpdate(), Constants.EDIT_RESULT);
    }

}
