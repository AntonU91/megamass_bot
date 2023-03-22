package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddTrainingResultHandler implements UserCallBackRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    UserService userService;
    AddNewExerciseHandler addNewExerciseHandler;


    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        if (userService.getExerciseList(userRequest.getChatId()).isEmpty()) {
            telegramService.sendMessage(userSession.getChatId(), "You don`t have any exercise⚠️.️");
            addNewExerciseHandler.handle(userRequest);
        } else {
            telegramService.sendMessage(userRequest.getChatId(), "For saving new result choose the exercise ⬇️",
                    keyboardHelper.exercisesList(userRequest.getChatId()));
            userSession.setState(ConversationState.CHOOSING_EXERCISE);
            userSessionService.saveUserSession(userRequest.getChatId(), userSession);
        }
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.WAITING_FOR_REQUEST)
                && isValidCallBack(userRequest.getUpdate(), Constants.ADD_NEW_RESULT);
    }
}
