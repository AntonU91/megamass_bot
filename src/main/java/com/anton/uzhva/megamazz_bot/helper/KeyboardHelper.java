package com.anton.uzhva.megamazz_bot.helper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardHelper {

    public InlineKeyboardMarkup mainMenu() {
        InlineKeyboardButton buttonSeeResults = new InlineKeyboardButton();
        InlineKeyboardButton buttonInsertNewResults = new InlineKeyboardButton();
        buttonSeeResults.setText("Посмотреть результаты");
        buttonSeeResults.setCallbackData("GET_RESULT");
        buttonInsertNewResults.setText("Внести новыe результаты");
        buttonInsertNewResults.setCallbackData("INSERT_RESULT");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row1.add(buttonSeeResults);
        row2.add(buttonInsertNewResults);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);
        return InlineKeyboardMarkup
                .builder()
                .keyboard(rowList).build();
    }

}
