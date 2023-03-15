package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.BodyWeightService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InputNewBodyWeightValueHandler implements UserCallBackRequestHandler {
    BodyWeightService bodyWeightService;
    UserSessionService userSessionService;
    TelegramService telegramService;

    @Autowired
    public InputNewBodyWeightValueHandler(BodyWeightService bodyWeightService, UserSessionService userSessionService, TelegramService telegramService) {
        this.bodyWeightService = bodyWeightService;
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        telegramService.sendMessage(userRequest.getChatId(), "Please, input your weight ✍️");
        userSession.setState(ConversationState.INPUTING_BODY_WEIGHT);
        userSessionService.saveUserSession(userRequest.getChatId(), userSession);
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.WAITING_FOR_REQUEST)
                && isValidCallBack(userRequest.getUpdate(), Constants.ADD_BODY_WEIGHT_VALUE);
    }
}
