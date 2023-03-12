package com.anton.uzhva.megamazz_bot.handler.command_handler;

import com.anton.uzhva.megamazz_bot.commands.BotCommands;
import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
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
    ExerciseService exerciseService;
    TelegramService telegramService;
    UserSessionService userSessionService;
    KeyboardHelper keyboardHelper;

    @Autowired
    public GetResultsFileCommandHandler(ExerciseService exerciseService, TelegramService telegramService, UserSessionService userSessionService, KeyboardHelper keyboardHelper) {
        this.exerciseService = exerciseService;
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isCommand(request.getUpdate(), BotCommands.GET_RESULTS_FILE);
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        telegramService.sendTextFileWithResults(request.getUpdate(), keyboardHelper.acceptInfo());
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        userSessionService.saveUserSession(request.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

}
