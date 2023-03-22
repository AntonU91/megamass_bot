package com.anton.uzhva.megamazz_bot.handler.commands_handler;

import com.anton.uzhva.megamazz_bot.commands.BotCommands;
import com.anton.uzhva.megamazz_bot.handler.AskUserToRegistHandler;
import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import com.anton.uzhva.megamazz_bot.util.UserRegistrationChecker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChangeLoginHandler extends UserRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    UserService userService;
    AskUserToRegistHandler askUserToRegistHandler;

    @Override
    public boolean isApplicable(UserRequest request) {
        return isCommand(request.getUpdate(), BotCommands.CHANGE_USER_LOGIN);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        if (!UserRegistrationChecker.isUserRegistered(userService, request.getChatId())) {
            askUserToRegistHandler.handle(request);
        } else {
            telegramService.sendMessage(request.getChatId(), String.format("Your current login is %s. Input new one ✍️."
                    , userService.getUserLogin(request.getChatId())));
            userSession.setState(ConversationState.СHANGING_USER_NAME);
            userSessionService.saveUserSession(userSession.getChatId(), userSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
