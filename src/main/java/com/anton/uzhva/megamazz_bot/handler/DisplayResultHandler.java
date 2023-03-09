package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.constant.Constants;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class DisplayResultHandler implements UserCallBackRequestHandler {
    TelegramService telegramService;
    UserSessionService userSessionService;
    ExerciseService exerciseService;
    KeyboardHelper keyboardHelper;

    @Autowired
    public DisplayResultHandler(TelegramService telegramService, UserSessionService userSessionService, ExerciseService exerciseService, KeyboardHelper keyboardHelper) {
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
        this.exerciseService = exerciseService;
        this.keyboardHelper = keyboardHelper;
    }

    @Override
    public void handleCallBack(UserRequest userRequest) {
        UserSession userSession = userSessionService.getSession(userRequest.getChatId());
        telegramService.sendMessage(userRequest.getChatId(), prepareResultsForDisplaying(userRequest.getUpdate()),
                keyboardHelper.acceptInfo());
        userSession.setState(ConversationState.WAITING_FOR_REQUEST);
        userSessionService.saveUserSession(userRequest.getChatId(), userSession);
    }

    @Override
    public boolean isCallbackApplicable(UserRequest userRequest) {
        return userRequest.getSession().getState().equals(ConversationState.CHOOSING_TRAINING_WEEK)
                && isValidCallBack(userRequest.getUpdate(), Constants.REGEX_TRAINING_WEEK);
    }

    private String prepareResultsForDisplaying(Update update) {
        StringBuilder results = new StringBuilder();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int weekNumber = Integer.parseInt(update.getCallbackQuery().getData().replace("WEEK-", ""));
        List<Exercise> exercisesResult = exerciseService.getTrainingResultOfConcreteWeek(chatId, weekNumber);
        results.append("Training week №").append(weekNumber).append("\n").append("\n");
        for (Exercise temp : exercisesResult) {
            results.append(String.format("%s - %.1f for %d time(s)\n", temp.getName(), temp.getWeight(),
                    temp.getCount()));
        }
        return results.toString();
    }

//    private SendMessage getTrainingResult(Update update) {
//        SendMessage message = new SendMessage();
//        StringBuilder results = new StringBuilder();
//        long chatId = update.getCallbackQuery().getMessage().getChatId();
//        Integer weekNumber = Integer.parseInt(update.getCallbackQuery().getData().replace("WEEK-", ""));
//        List<Exercise> exercisesResult = exerciseService.getTrainingResultOfConcreteWeek(chatId, weekNumber);
//        results.append("Тренировочная неделя №").append(weekNumber).append("\n").append("\n");
//        for (Exercise temp : exercisesResult) {
//            results.append(String.format("Упражнение %s - %.1f кг на %d раз\n", temp.getName(), temp.getWeight(),
//                    temp.getCount()));
//        }
//        message.setChatId(chatId);
//        message.setText(results.toString());
//        message.setReplyMarkup(acceptInfo());
//        return message;
//    }
}
