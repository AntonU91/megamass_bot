package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.model.UserRequest;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserCallBackRequestHandler {
    void handleCallBack(UserRequest userRequest);

    default boolean isValidCallBack(Update update, String callBackData) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData().matches(callBackData);
    }

    boolean isCallbackApplicable(UserRequest userRequest);
}
