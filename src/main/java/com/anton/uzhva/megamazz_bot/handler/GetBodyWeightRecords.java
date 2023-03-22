package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.BodyWeightService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetBodyWeightRecords implements UserCallBackRequestHandler {
    BodyWeightService bodyWeightService;
    KeyboardHelper keyboardHelper;
    TelegramService telegramService;
    UserSessionService userSessionService;

    @Autowired
    public GetBodyWeightRecords(BodyWeightService bodyWeightService, KeyboardHelper keyboardHelper, TelegramService telegramService, UserSessionService userSessionService) {
        this.bodyWeightService = bodyWeightService;
        this.keyboardHelper = keyboardHelper;
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        if (bodyWeightService.hasAtLeastOneRecord(userRequest.getChatId())) {
            telegramService.sendMessage(userRequest.getChatId(), "Choose the period⬇️",
                    keyboardHelper.periodOfBodyWeightsRecordToChoose(userRequest.getChatId()));
        } else {
            telegramService.sendMessage(userRequest.getChatId(), "You have not records yet⚠️", keyboardHelper.acceptInfo());
        }
        userSession.setState(ConversationState.BODY_WEIGHT_OPTION);
        userSessionService.saveUserSession(userSession.getChatId(), userSession);
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.BODY_WEIGHT_OPTION)
                && isValidCallBack(userRequest.getUpdate(), Constants.GET_BODY_WEIGHT_VALUES);
    }
}
