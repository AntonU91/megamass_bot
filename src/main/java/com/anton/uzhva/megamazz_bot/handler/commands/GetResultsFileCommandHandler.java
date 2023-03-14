package com.anton.uzhva.megamazz_bot.handler.commands;

import com.anton.uzhva.megamazz_bot.commands.BotCommands;
import com.anton.uzhva.megamazz_bot.handler.AskUserToRegistHandler;
import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import com.anton.uzhva.megamazz_bot.util.UserRegistrationChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class GetResultsFileCommandHandler extends UserRequestHandler {
    UserService userService;
    TelegramService telegramService;
    UserSessionService userSessionService;
    KeyboardHelper keyboardHelper;
    AskUserToRegistHandler askUserToRegistHandler;

    @Autowired
    public GetResultsFileCommandHandler(UserService userService, TelegramService telegramService, UserSessionService userSessionService, KeyboardHelper keyboardHelper, AskUserToRegistHandler askUserToRegistHandler) {
        this.userService = userService;
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
        this.askUserToRegistHandler = askUserToRegistHandler;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isCommand(request.getUpdate(), BotCommands.GET_RESULTS_FILE);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        if (!UserRegistrationChecker.isUserRegistered(userService, request.getChatId())) {
            askUserToRegistHandler.handle(request);
        } else {
            telegramService.sendTextFileWithResults(request.getUpdate(), keyboardHelper.acceptInfo());
            userSession.setState(ConversationState.WAITING_FOR_REQUEST);
            userSessionService.saveUserSession(request.getChatId(), userSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

}
