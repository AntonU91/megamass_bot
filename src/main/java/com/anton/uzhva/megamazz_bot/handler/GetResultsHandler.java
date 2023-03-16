package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
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
public class GetResultsHandler implements UserCallBackRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    ExerciseService exerciseService;

    @Autowired
    public GetResultsHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper, ExerciseService exerciseService) {
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.exerciseService = exerciseService;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        long resultsCount = (long) exerciseService.getExercisesCountByUserID(userRequest.getChatId());
        if (resultsCount == 0) {
            telegramService.sendMessage(userRequest.getChatId(), "You have not added any training result yet", keyboardHelper.acceptInfo());
        } else {
            telegramService.sendMessage(userRequest.getChatId(), "Choose training week to watch results", keyboardHelper.listOfTrainingWeeks(userRequest.getChatId()));
            userSession.setState(ConversationState.CHOOSING_TRAINING_WEEK);
            userSessionService.saveUserSession(userRequest.getChatId(), userSession);
        }
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.WAITING_FOR_REQUEST)
                && isValidCallBack(userRequest.getUpdate(), Constants.GET_BODY_WEIGHT_VALUES);
    }

}
