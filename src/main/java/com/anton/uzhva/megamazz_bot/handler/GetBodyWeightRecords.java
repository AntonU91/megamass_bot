package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.service.BodyWeightService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetBodyWeightRecords implements UserCallBackRequestHandler {
    BodyWeightService bodyWeightService;
    KeyboardHelper keyboardHelper;
    TelegramService telegramService;

    @Autowired
    public GetBodyWeightRecords(BodyWeightService bodyWeightService, KeyboardHelper keyboardHelper, TelegramService telegramService) {
        this.bodyWeightService = bodyWeightService;
        this.keyboardHelper = keyboardHelper;
        this.telegramService = telegramService;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        telegramService.sendMessage(userRequest.getChatId(), "Choose the period",
                keyboardHelper.periodOfBodyWeightsRecordToChoose(userRequest.getChatId()));
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.BODY_WEIGHT_OPTION)
                && isValidCallBack(userRequest.getUpdate(), Constants.GET_BODY_WEIGHT_VALUES);
    }
}
