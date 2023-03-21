package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
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
public class ChoosingExerciseHandler extends UserRequestHandler {
    UserService userService;
    TelegramService telegramService;
    UserSessionService userSessionService;
    ChooseExerciseToDeleteHandler chooseExerciseToDeleteHandler;

    @Autowired
    public ChoosingExerciseHandler(UserService userService, TelegramService telegramService, UserSessionService userSessionService, ChooseExerciseToDeleteHandler chooseExerciseToDeleteHandler) {
        this.userService = userService;
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.chooseExerciseToDeleteHandler = chooseExerciseToDeleteHandler;
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
        /**
         * if update has text equals to "Delete exercise" processing will be forwarded to {@link ChooseExerciseToDeleteHandler}
         */
        if (request.getUpdate().getMessage().getText().equals(Constants.DELETE_EXERCISE)) {
            chooseExerciseToDeleteHandler.handle(request);
        } else {
            Exercise exercise = new Exercise();
            exercise.setName(request.getUpdate().getMessage().getText());
            userSession.setExercise(exercise);
            telegramService.sendMessage(request.getChatId(), "Send the the weight you worked with in format of digit. You can use decimal number.\n For example, 65.92");

            userSession.setState(ConversationState.INPUTTING_WEIGHT);
            userSessionService.saveUserSession(request.getChatId(), userSession);
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
    /**
     * Return specified by first argument exercise name of concrete registered user.
     * If first argument equals "Delete exercise" method return these words.
     * In other cases {@link NoSuchElementException} will be thrown.
     * @param message
     * @param chatId
     * @return specified by first argument exercise name
     * @throws NoSuchElementException
     */
    String getSelectedExerciseName(String message, long chatId) {
        for (String exercise : userService.getExerciseList(chatId)) {
            if (message.matches("\\s*" + exercise)) {
                return exercise;
            } else if (message.matches(Constants.DELETE_EXERCISE)) {
                return Constants.DELETE_EXERCISE;
            }
        }
        throw new NoSuchElementException();
    }
}
