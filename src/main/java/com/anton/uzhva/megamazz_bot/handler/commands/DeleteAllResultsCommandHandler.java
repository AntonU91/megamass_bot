package com.anton.uzhva.megamazz_bot.handler.commands;

import com.anton.uzhva.megamazz_bot.commands.BotCommands;
import com.anton.uzhva.megamazz_bot.handler.AskUserToRegistHandler;
import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import com.anton.uzhva.megamazz_bot.util.UserRegistrationChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllResultsCommandHandler extends UserRequestHandler {
    TelegramService telegramService;
    UserSessionService userSessionService;
    KeyboardHelper keyboardHelper;
    UserService userService;
    AskUserToRegistHandler askUserToRegistHandler;

    @Autowired
    public DeleteAllResultsCommandHandler(TelegramService telegramService, UserSessionService userSessionService, KeyboardHelper keyboardHelper, UserService userService, AskUserToRegistHandler askUserToRegistHandler) {
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
        this.userService = userService;
        this.askUserToRegistHandler = askUserToRegistHandler;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isCommand(request.getUpdate(), BotCommands.DELETE_ALL_RESULTS);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        if (!UserRegistrationChecker.isUserRegistered(userService, request.getChatId())) {
            askUserToRegistHandler.handle(request);
        } else {
            telegramService.sendMessage(request.getChatId(), "You want to delete all training records, are you sure?\n" + "I recommend saving the results before deleting with the \"/getresultsfile\" command", keyboardHelper.acceptOrCancel());
            userSession.setState(ConversationState.WAITING_FOR_REQUEST);
            userSessionService.saveUserSession(request.getChatId(), userSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
