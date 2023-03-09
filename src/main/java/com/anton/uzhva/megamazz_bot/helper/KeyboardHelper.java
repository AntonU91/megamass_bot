package com.anton.uzhva.megamazz_bot.helper;

import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardHelper {
    UserService userService;
    ExerciseService exerciseService;

    @Autowired
    public KeyboardHelper(UserService userService, ExerciseService exerciseService) {
        this.userService = userService;
        this.exerciseService = exerciseService;
    }

    public InlineKeyboardMarkup mainMenu() {
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("Watch results")
                .callbackData("GET_RESULT")
                .build());
        row2.add(InlineKeyboardButton.builder()
                .text("Add new result")
                .callbackData("ADD_RESULT")
                .build());
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);
        return InlineKeyboardMarkup
                .builder()
                .keyboard(rowList).build();
    }

    public ReplyKeyboardMarkup exercisesList(long chatId) {
        List<String> exerciseNamesList = userService.getExerciseList(chatId);
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rowList = new ArrayList<>();
         for (int i = 0; i < exerciseNamesList.size(); i++) {
            row.add(KeyboardButton.builder()
                    .text(exerciseNamesList.get(i))
                    .build());
            if ((i + 1) % 2 == 0) {
                rowList.add(row);
                row = new KeyboardRow();
            }
            if (i + 1 == exerciseNamesList.size()) {
                rowList.add(row);
            }
        }
        KeyboardRow rowForDeletingAndAddingExercise = new KeyboardRow();
        rowForDeletingAndAddingExercise.add(KeyboardButton.builder()
                .text("Add exercise")
                .build());
        rowForDeletingAndAddingExercise.add(KeyboardButton.builder()
                .text("Delete exercise")
                .build());
        rowList.add(rowForDeletingAndAddingExercise);
        return ReplyKeyboardMarkup.builder()
                .keyboard(rowList)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public InlineKeyboardMarkup countKeyBoard() {
        List<List<InlineKeyboardButton>> rowlist = new ArrayList<>();
        int count = 1;
        for (int i = 0; i < 10; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                InlineKeyboardButton countButton = new InlineKeyboardButton();
                countButton.setText(String.valueOf(count));
                countButton.setCallbackData(String.valueOf(count));
                row.add(countButton);
                count++;
            }
            rowlist.add(row);
        }
        return InlineKeyboardMarkup.builder()
                .keyboard(rowlist)
                .build();
    }

    public InlineKeyboardMarkup acceptOrChangeResultValue() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("OK")
                .callbackData("OK")
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("Edit")
                .callbackData("EDIT")
                .build());
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);
        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();
    }

    public InlineKeyboardMarkup listOfTrainingWeeks(long chatId) {
        List<Integer> trainingWeekNumberList = exerciseService.getListOfTrainingWeeksNumber(chatId);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < trainingWeekNumberList.size(); i++) {
            row.add(InlineKeyboardButton
                    .builder()
                    .text(String.valueOf(trainingWeekNumberList.get(i)))
                    .callbackData("WEEK-" + trainingWeekNumberList.get(i))
                    .build());
            if ((i + 1) % 3 == 0) {
                rowList.add(row);
                row = new ArrayList<>();
            }
            if (i + 1 == trainingWeekNumberList.size()) {
                rowList.add(row);
            }
        }
        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();
    }

    public InlineKeyboardMarkup acceptInfo() {
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("✅Ok")
                .callbackData("OK")
                .build());
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();
    }

    public ReplyKeyboardMarkup listOfExercisesToDelete(long chatId) {
        List<String> exerciseNamesList = userService.getExerciseList(chatId);
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rowList = new ArrayList<>();
        for (int i = 0; i < exerciseNamesList.size(); i++) {
            row.add(KeyboardButton.builder()
                    .text(exerciseNamesList.get(i))
                    .build());
            if ((i + 1) % 2 == 0) {
                rowList.add(row);
                row = new KeyboardRow();
            }
            if (i + 1 == exerciseNamesList.size()) {
                rowList.add(row);
            }
        }
        return ReplyKeyboardMarkup.builder()
                .keyboard(rowList)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }
}
