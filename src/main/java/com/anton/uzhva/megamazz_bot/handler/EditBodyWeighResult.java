package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.BodyWeightService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EditBodyWeighResult implements UserCallBackRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    BodyWeightService bodyWeightService;
    UserService userService;

    @Autowired
    public EditBodyWeighResult(UserSessionService userSessionService, TelegramService telegramService,
                               KeyboardHelper keyboardHelper, BodyWeightService bodyWeightService, UserService userService) {
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.bodyWeightService = bodyWeightService;
        this.userService = userService;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        telegramService.sendMessage(userRequest.getChatId(),
                String.format("To edit you current body weight result (%.2f kg) input new value ✍️", userSession.getBodyWeight().value()));
        userSession.setState(ConversationState.INPUTTING_BODY_WEIGHT);
        userSessionService.saveUserSession(userSession.getChatId(), userSession);
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.BODY_WEIGHT_OPTION)
                && isValidCallBack(userRequest.getUpdate(), Constants.EDIT_RESULT);
    }
}
