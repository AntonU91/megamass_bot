package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.util.NoSuchElementException;

@Component
public class DeleteSpecifiedExerciseHandler extends UserRequestHandler {
    UserService userService;
    TelegramService telegramService;
    UserSessionService userSessionService;
    KeyboardHelper keyboardHelper;

    @Autowired
    public DeleteSpecifiedExerciseHandler(UserService userService, TelegramService telegramService, UserSessionService userSessionService, KeyboardHelper keyboardHelper) {
        this.userService = userService;
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.keyboardHelper = keyboardHelper;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        String exerciseToDelete = request.getUpdate().getMessage().getText();
        return request.getSession().getState().equals(ConversationState.DELETING_EXERCISE)
                && isValidTextMessage(request.getUpdate(), getSelectedExerciseName(exerciseToDelete, request.getChatId()));
    }

    @Override
    public void handle(UserRequest request) {
        String exerciseToDelete = request.getUpdate().getMessage().getText();
        UserSession userSession = userSessionService.getSession(request.getChatId());
        userService.deleteSpecifiedExerciseByUserID(exerciseToDelete, request.getChatId());
        telegramService.sendMessage(request.getChatId(), String.format("Exercise \"%s\" was deleted ", exerciseToDelete),
                keyboardHelper.acceptInfo());
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        userSessionService.saveUserSession(request.getChatId(), userSession);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    String getSelectedExerciseName(String message, long chatId) {
        for (String exercise : userService.getExerciseList(chatId)) {
            if (message.matches("\\s*" + exercise)) {
                return exercise;
            }
        }
        throw new NoSuchElementException();
    }
}
