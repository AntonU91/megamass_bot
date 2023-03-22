package com.anton.uzhva.megamazz_bot.helper;

import com.anton.uzhva.megamazz_bot.constant.Constants;
import com.anton.uzhva.megamazz_bot.service.BodyWeightService;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.anton.uzhva.megamazz_bot.util.Period;
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
    BodyWeightService bodyWeightService;

    @Autowired
    public KeyboardHelper(UserService userService, ExerciseService exerciseService, BodyWeightService bodyWeightService) {
        this.userService = userService;
        this.exerciseService = exerciseService;
        this.bodyWeightService = bodyWeightService;
    }

    public InlineKeyboardMarkup mainMenu() {
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("\uD83D\uDC40"+Constants.GET_RESULTS)
                .callbackData(Constants.GET_RESULTS)
                .build());
        row2.add(InlineKeyboardButton.builder()
                .text("➕"+Constants.ADD_NEW_RESULT)
                .callbackData(Constants.ADD_NEW_RESULT)
                .build());
        row3.add(InlineKeyboardButton.builder()
                .text(Constants.BODY_WEIGHT)
                .callbackData(Constants.BODY_WEIGHT)
                .build());
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);
        rowList.add(row3);
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
                .text("➕Add exercise")
                .build());
        rowForDeletingAndAddingExercise.add(KeyboardButton.builder()
                .text("\uD83D\uDDD1️Delete exercise")
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
                .text("✅OK")
                .callbackData(Constants.OK)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text( "\uD83D\uDCDD"+Constants.EDIT_RESULT)
                .callbackData(Constants.EDIT_RESULT)
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
        List<InlineKeyboardButton> rowBack = new ArrayList<>();
        rowBack.add(InlineKeyboardButton.builder()
                .text("\uD83D\uDD19"+Constants.BACK)
                .callbackData(Constants.BACK)
                .build());
        rowList.add(rowBack);

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < trainingWeekNumberList.size(); i++) {
            row.add(InlineKeyboardButton
                    .builder()
                    .text(String.valueOf(trainingWeekNumberList.get(i)))
                    .callbackData("WEEK-" + trainingWeekNumberList.get(i))
                    .build());
            if ((i + 1) % 4 == 0) {
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
                .callbackData(Constants.OK)
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

    public InlineKeyboardMarkup acceptOrCancel() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("✅Yes")
                .callbackData(Constants.DELETE_ALL_RESULTS)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("❌Cancel")
                .callbackData(Constants.CANCEL)
                .build());
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);
        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();
    }

    public InlineKeyboardMarkup weightMenu() {
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("➕"+Constants.ADD_BODY_WEIGHT_VALUE)
                .callbackData(Constants.ADD_BODY_WEIGHT_VALUE)
                .build());
        row2.add(InlineKeyboardButton.builder()
                .text("\uD83D\uDC40"+Constants.GET_BODY_WEIGHT_VALUES)
                .callbackData(Constants.GET_BODY_WEIGHT_VALUES)
                .build());
        row3.add(InlineKeyboardButton.builder()
                .text("🔙"+Constants.BACK)
                .callbackData(Constants.BACK)
                .build());

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);
        rowList.add(row3);
        return InlineKeyboardMarkup.builder()
                .keyboard(rowList)
                .build();
    }

    public ReplyKeyboardMarkup periodOfBodyWeightsRecordToChoose(long chatId) {
        Period[] periodToGetResults = Period.values();
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rowList = new ArrayList<>();
        row.add(KeyboardButton.builder()
                .text("Last result")
                .build());
        for (int i = 0; i < periodToGetResults.length; i++) {
            boolean hasAtLeastOneRecordInSpecifiedDiapason = bodyWeightService
                    .matchSpecifiedDiapason(chatId, periodToGetResults[i].getDays());
            if (hasAtLeastOneRecordInSpecifiedDiapason) {
                row.add(KeyboardButton.builder()
                        .text(periodToGetResults[i].getName())
                        .build());
            } else continue;
            if ((i + 1) % 2 == 0) {
                rowList.add(row);
                row = new KeyboardRow();
            }
            if (i + 1 == periodToGetResults.length) {
                rowList.add(row);
            }
        }
        row.add(KeyboardButton.builder()
                .text("All results")
                .build());
        rowList.add(row);
        return ReplyKeyboardMarkup.builder()
                .keyboard(rowList)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }
}
