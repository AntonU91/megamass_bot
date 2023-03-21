package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaveWeightHandler extends UserRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    WrongWeighFormatHandler wrongWeighFormatHandler;

    @Override
    public boolean isApplicable(UserRequest request) {
        return request.getSession().getState().equals(ConversationState.INPUTTING_WEIGHT);
    }

    @Override
    public void handle(UserRequest userRequest) {
        String retrievedText = userRequest.getUpdate().getMessage().getText();
        if (!retrievedText.matches(Constants.REGEX_INPUTTED_WEIGHT)) {
            wrongWeighFormatHandler.handle(userRequest);
        }
        else {
            double weight = Double.parseDouble(retrievedText);
            UserSession userSession = userSessionService.getSession(userRequest.getChatId());
            Exercise exercise = userSession.getExercise();
            exercise.setWeight(weight);
            userSession.setState(ConversationState.WAITING_REPEATING_COUNT);
            userSessionService.saveUserSession(userSession.getChatId(), userSession);
            telegramService.sendMessage(userRequest.getChatId(), "Select the number of exercise repetitions",
                    keyboardHelper.countKeyBoard());
       }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
