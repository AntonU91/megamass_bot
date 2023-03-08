package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class ChoosingExerciseHelper extends UserRequestHandler {
    UserService userService;
    TelegramService telegramService;
    UserSessionService userSessionService;

    @Autowired
    public ChoosingExerciseHelper(UserService userService, TelegramService telegramService, UserSessionService userSessionService) {
        this.userService = userService;
        this.telegramService = telegramService;
        this.userSessionService =  userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        String exerciseName = request.getUpdate().getMessage().getText();
        return request.getSession().getState().equals(ConversationState.CHOOSING_EXERCISE)
                && isValidTextMessage(request.getUpdate(), getSelectedExerciseName(exerciseName, request.getChatId()));

    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        Exercise exercise = new Exercise();
        exercise.setName(request.getUpdate().getMessage().getText());
        userSession.setExercise(exercise);
        telegramService.sendMessage(request.getChatId(),  "Send the the weight you worked with");

        userSession.setState(ConversationState.INPUTING_RESULT_WEIGHT);
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
