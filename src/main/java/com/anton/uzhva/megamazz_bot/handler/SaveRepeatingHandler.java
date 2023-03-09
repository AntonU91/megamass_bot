package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class SaveRepeatingHandler implements UserCallBackRequestHandler {
    UserSessionService userSessionService;
    TelegramService telegramService;
    KeyboardHelper keyboardHelper;
    ExerciseService exerciseService;
    UserService userService;

    public SaveRepeatingHandler(UserSessionService userSessionService, TelegramService telegramService, KeyboardHelper keyboardHelper, ExerciseService exerciseService, UserService userService) {
        this.userSessionService = userSessionService;
        this.telegramService = telegramService;
        this.keyboardHelper = keyboardHelper;
        this.exerciseService = exerciseService;
        this.userService = userService;
    }


    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        Exercise exercise = userSession.getExercise();
        exercise.setUser(userService.findUserById(userRequest.getChatId()).get());
        exercise.setCount(Integer.parseInt(userRequest
                .getUpdate()
                .getCallbackQuery()
                .getData()));
        exercise.setRecordDate(new Date());
        if (exerciseService.findAtLeastOneExerciceRecordByUserId(userRequest.getChatId()).isEmpty()) {
            exercise.setWeekNumber(1);
        } else {
            exercise.setWeekNumber(defineTheWeeksOfTraining(exercise.getRecordDate(), userRequest.getChatId()));
        }
        exerciseService.saveExercise(exercise);
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        userSessionService.saveUserSession(userRequest.getChatId(), userSession);
        telegramService.editMessage(userRequest.getUpdate(), String.format("Nice! Your result in exercise %s - %.1f kg for %d times",
                exercise.getName(), exercise.getWeight(), exercise.getCount()), keyboardHelper.acceptOrChangeResultValue());
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.WAITING_REPEATING_COUNT) &&
                isValidCallBack(userRequest.getUpdate(), Constants.REGEX_REPEATING_COUNT);
    }

    private int defineTheWeeksOfTraining(Date date, long chatId) {
        Exercise exercise = (Exercise) exerciseService.getTheErliestRecord(chatId);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar2.setTime(exercise.getRecordDate());
        int result = gregorianCalendar.get(Calendar.WEEK_OF_YEAR) - gregorianCalendar2.get(Calendar.WEEK_OF_YEAR);
        return result + 1;
    }


}