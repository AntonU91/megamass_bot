package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.handler.commands_handler.CancelCommandHandler;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelButtonHandler implements UserCallBackRequestHandler {
    CancelCommandHandler cancelCommandHandler;

    @Autowired
    public CancelButtonHandler(CancelCommandHandler cancelCommandHandler) {
        this.cancelCommandHandler = cancelCommandHandler;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        cancelCommandHandler.handle(userRequest);
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.WAITING_FOR_REQUEST)
                && isValidCallBack(userRequest.getUpdate(), Constants.CANCEL);
    }
}
