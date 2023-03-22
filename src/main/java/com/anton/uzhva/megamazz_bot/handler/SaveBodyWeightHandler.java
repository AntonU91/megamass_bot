package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.*;
import com.anton.uzhva.megamazz_bot.service.BodyWeightService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor
public class SaveBodyWeightHandler extends UserRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    BodyWeightService bodyWeightService;
    UserService userService;
    WrongWeighFormatHandler wrongWeighFormatHandler;


    @Override
    public boolean isApplicable(UserRequest request) {
        return request.getSession().getState().equals(ConversationState.INPUTTING_BODY_WEIGHT);
              //  && isValidTextMessage(request.getUpdate(), Constants.REGEX_INPUTTED_WEIGHT);
    }

    @Override
    public void handle(UserRequest request) {
        String retrievedText = request.getUpdate().getMessage().getText();
        if (!retrievedText.matches(Constants.REGEX_INPUTTED_WEIGHT)) {
            wrongWeighFormatHandler.handle(request);
        }
        else {
            UserSession userSession = userSessionService.getSession(request.getChatId());
            double value = Double.parseDouble(request.getUpdate().getMessage().getText());
            User user = userService.getUserById(request.getChatId()).get();
            BodyWeight bodyWeight = userSession.getBodyWeight()
                    .user(user)
                    .value(value)
                    .createdAt(new Date());

            bodyWeightService.saveBodyWeight(bodyWeight);
            userSession.setState(ConversationState.BODY_WEIGHT_OPTION);
            telegramService.sendMessage(request.getChatId(), String.format("New body weight result is %.2f kg\uD83D\uDD25", value),
                    keyboardHelper.acceptOrChangeResultValue());
            userSessionService.saveUserSession(request.getChatId(), userSession);
        }

    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
