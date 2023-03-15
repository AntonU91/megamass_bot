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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelCommandHandler extends UserRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    UserService userService;
    AskUserToRegistHandler askUserToRegistHandler;

    @Autowired
    public CancelCommandHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper, UserService userService, AskUserToRegistHandler askUserToRegistHandler) {
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.userService = userService;
        this.askUserToRegistHandler = askUserToRegistHandler;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isCommand(request.getUpdate(), BotCommands.CANCEL);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        if (!UserRegistrationChecker.isUserRegistered(userService, request.getChatId())) {
            askUserToRegistHandler.handle(request);
        } else {
            telegramService.sendMessage(request.getChatId(), "Moving on!", keyboardHelper.mainMenu());
            userSession.setState(ConversationState.WAITING_FOR_REQUEST);
            userSessionService.saveUserSession(request.getChatId(), userSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
