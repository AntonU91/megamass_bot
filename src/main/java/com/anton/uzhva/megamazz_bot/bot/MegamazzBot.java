package com.anton.uzhva.megamazz_bot.bot;

import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.ExerciseRepo;
import com.anton.uzhva.megamazz_bot.model.User;
import com.anton.uzhva.megamazz_bot.model.UserRepo;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MegamazzBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUserName;
    @Value("${bot.token}")
    private String botToken;
    @Autowired
    UserRepo userRepo;

    @Autowired
    ExerciseRepo exerciseRepo;


    @Override
    public void onRegister() {
        System.out.println("Bot is registered");
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String userMsg = update.getMessage().getText();
            divider(userMsg, chatId, update);
        } else if (update.hasCallbackQuery()) {
            processingCallBackQuery(update);
        }
    }

    public SendMessage greetingToUnregisteredUser(Long chatId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(EmojiParser.parseToUnicode(String.format("Привет, %s. Плиз, придумай свой логин:muscle:" +
                "\nЛогин должен начинаться с буквы", update.getMessage().getFrom().getFirstName())));
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage greetingToExistUser(Long chatId, String userLogin) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(String.format("Привет, %s!", userLogin));
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSeeResults = new InlineKeyboardButton();
        InlineKeyboardButton buttonInsertNewResults = new InlineKeyboardButton();
        buttonSeeResults.setText("Посмотреть результаты");
        buttonSeeResults.setCallbackData("GET_RESULT");
        buttonInsertNewResults.setText("Внести новые результаты");
        buttonInsertNewResults.setCallbackData("INSERT_RESULT");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonSeeResults);
        row.add(buttonInsertNewResults);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public void registration(Long chatId, Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (userRepo.findById(update.getMessage().getChatId()).isPresent()) {
                return;
            }
            User user = new User();
            String userLogin = update.getMessage().getText();
            user.setId(chatId);
            user.setUserLogin(userLogin);
            userRepo.save(user);
            executeMsg(greetingToExistUser(chatId, userLogin));
        }
    }


    public void executeMsg(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void divider(String msg, Long chatId, Update update) {

        if (msg.matches("/start")) {
            Optional<User> user = userRepo.findById(update.getMessage().getChatId());
            if (user.isPresent()) {
                String userLogin = user.get().getUserLogin();
                executeMsg(greetingToExistUser(chatId, userLogin));
            } else {
                executeMsg(greetingToUnregisteredUser(chatId, update));
            }
        } else if (msg.matches("^\\s*[Aa-zZаА-Яя].*")) {
            registration(chatId, update);
        } else if (msg.matches("\\s*\\d{1,3}.*")) {
            saveWeightValue(msg, chatId);
            executeMsg(selectCountExerciseRepeating(chatId));
        }

    }

    public InlineKeyboardMarkup selectExerciseKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton barbellPush = new InlineKeyboardButton();
        InlineKeyboardButton barbellSquat = new InlineKeyboardButton();
        InlineKeyboardButton deadlift = new InlineKeyboardButton();

        barbellPush.setText("Жим");
        barbellPush.setCallbackData("PUSH");
        barbellSquat.setText("Присед");
        barbellSquat.setCallbackData("SQUAT");
        deadlift.setText("Становая тяга");
        deadlift.setCallbackData("DEAD_LIFT");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(barbellPush);
        row1.add(barbellSquat);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(deadlift);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        rowList.add(row2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private void executeEditMsgText(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage selectCountExerciseRepeating(Long chatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Укажи количество раз");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(countKeyBoard());
        return sendMessage;
    }

    public InlineKeyboardMarkup countKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowlist = new ArrayList<>();
        int count = 1;
        for (int i = 0; i < 5; i++) {
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
        inlineKeyboardMarkup.setKeyboard(rowlist);
        return inlineKeyboardMarkup;
    }

    public void processingCallBackQuery(Update update) {

        String callBackQuery = update.getCallbackQuery().getData();
        switch (callBackQuery) {
            case "INSERT_RESULT":
                EditMessageText messageText = new EditMessageText();
                messageText.setText("Выбери категорию");
                messageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
                messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                messageText.setReplyMarkup(selectExerciseKeyBoard());
                executeEditMsgText(messageText);
                break;
            case "PUSH":
                messageText = new EditMessageText();
                messageText.setText("Введи вес с клавиатуры");
                messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                messageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
                executeEditMsgText(messageText);
        }
    }

    public void saveWeightValue(String msg, Long chatId) {

        User user = userRepo.findById(chatId).get();
        Exercise exercise = new Exercise();
        exercise.setWeight(Double.parseDouble(msg));
        exercise.setUser(user);
        exerciseRepo.save(exercise);

    }
}



