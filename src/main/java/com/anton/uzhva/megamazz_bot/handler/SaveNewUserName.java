package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.User;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaveNewUserName extends UserRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    UserService userService;


    @Override
    public boolean isApplicable(UserRequest request) {
        return request.getSession().getState().equals(ConversationState.СHANGING_USER_NAME)
                && isValidTextMessage(request.getUpdate(), Constants.REGEX_USER_LOGIN);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        User user = userService.getUserById(request.getChatId()).get();
        user.setUserLogin(request.getUpdate().getMessage().getText());
        userService.saveUser(user);
        telegramService.sendMessage(userSession.getChatId(), String.format("You new name is %s", user.getUserLogin()), keyboardHelper.acceptInfo());
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        userSessionService.saveUserSession(request.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
