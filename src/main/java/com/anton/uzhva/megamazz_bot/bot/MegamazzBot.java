package com.anton.uzhva.megamazz_bot.bot;

import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.ExerciseRepo;
import com.anton.uzhva.megamazz_bot.model.User;
import com.anton.uzhva.megamazz_bot.model.UserRepo;
import com.anton.uzhva.megamazz_bot.util.ExerciseName;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
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

    private Long resultId = 0L;

    private ExerciseName exerciseName;


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
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(selector());
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
        } else if (msg.matches("^\\s*\\D+.*")) {
            registration(chatId, update);
        } else if (msg.matches("\\s*\\d{1,3}.*")) {
            saveWeightValue(msg);
            executeMsg(selectCountExerciseRepeating(chatId));
        }

    }

    public InlineKeyboardMarkup selectExerciseKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ExerciseName[] exerciseNames = ExerciseName.values();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (int i = 0; i < exerciseNames.length; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(exerciseNames[i].getExrcsName());
            button.setCallbackData(exerciseNames[i].name());
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rowList.add(row);
                row = new ArrayList<>();
            }
            if (i + 1 == exerciseNames.length) {
                rowList.add(row);
            }
        }
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
        String callBackQueryData = update.getCallbackQuery().getData();

        if (callBackQueryData.equals("INSERT_RESULT")) {
            executeEditMsgText(prepareForExerciseSelection(update));
        } else if (isCallBackQueryExerciseName(callBackQueryData)) {
            executeEditMsgText(createResultRecordAndPrepareForGettingValues(update, callBackQueryData));
        } else if (callBackQueryData.matches("\\d{1,3}")) {
            saveCountValue(update);
            executeEditMsgText(showExerciseResult(update));

        } else if (callBackQueryData.equals("OK")) {
            executeMsg(acceptResultIndicators(update));

        } else if (callBackQueryData.matches("EDIT")) {
            executeMsg(editResultValue(update));
        }
    }

    private EditMessageText prepareForExerciseSelection(Update update) {
        EditMessageText messageText = new EditMessageText();
        messageText.setText("Выбери категорию");
        messageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
        messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        messageText.setReplyMarkup(selectExerciseKeyBoard());
        return messageText;
    }

    private EditMessageText createResultRecordAndPrepareForGettingValues(Update update, String callBackQueryData) {

        EditMessageText messageText = new EditMessageText();
        User user = userRepo.findById(update.getCallbackQuery().getMessage().getChatId()).get();
        Exercise exercise = new Exercise();
        exercise.setName(ExerciseName.valueOf(callBackQueryData));
        exercise.setUser(user);

        exerciseRepo.save(exercise);
        resultId = exercise.getId();
        messageText.setText("Введи максимальный весовой результат с клавиатуры");
        messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        messageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return messageText;
    }

    SendMessage editResultValue(Update update) {
        SendMessage messageText = new SendMessage();
        Exercise exercise = exerciseRepo.findById(resultId).get();
        messageText.setText(String.format("Для редактирования упражнения \"%s\" введи максимальный весовой результат с клавиатуры"
                , exercise.getName().getExrcsName()));
        messageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return messageText;
    }


    private SendMessage acceptResultIndicators(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Продолжаем!");
        sendMessage.setReplyMarkup(selector());
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        return sendMessage;
    }

    public void saveWeightValue(String msg) {
        Exercise exercise = exerciseRepo.findById(resultId).get();
        exercise.setWeight(Double.parseDouble(msg));
        exerciseRepo.save(exercise);
    }

    public void saveCountValue(Update update) {
        Exercise exercise = exerciseRepo.findById(resultId).get();
        int count = Integer.parseInt(update.getCallbackQuery().getData());
        exercise.setCount(count);
        exerciseRepo.save(exercise);
    }

    public EditMessageText showExerciseResult(Update update) {
        EditMessageText editMessageText = new EditMessageText();
        Exercise exercise = exerciseRepo.findById(resultId).get();
        editMessageText.setText(String.format("Отлично! Твой результат в упражнение %s - %.1f кг на %d раз",
                exercise.getName().getExrcsName(), exercise.getWeight(), exercise.getCount()));
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setChatId(update.getCallbackQuery().getFrom().getId());
        editMessageText.setReplyMarkup(acceptOrChangeResultValue());
        return editMessageText;
    }

    public InlineKeyboardMarkup selector() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSeeResults = new InlineKeyboardButton();
        InlineKeyboardButton buttonInsertNewResults = new InlineKeyboardButton();
        buttonSeeResults.setText("Посмотреть\nрезультаты");
        buttonSeeResults.setCallbackData("GET_RESULT");
        buttonInsertNewResults.setText("Внести новыe\n результаты");
        buttonInsertNewResults.setCallbackData("INSERT_RESULT");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonSeeResults);
        row.add(buttonInsertNewResults);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup acceptOrChangeResultValue() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSeeResults = new InlineKeyboardButton();
        InlineKeyboardButton buttonInsertNewResults = new InlineKeyboardButton();
        buttonSeeResults.setText("OK");
        buttonSeeResults.setCallbackData("OK");
        buttonInsertNewResults.setText("Редактировать");
        buttonInsertNewResults.setCallbackData("EDIT");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonSeeResults);
        row.add(buttonInsertNewResults);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;

    }

    boolean isCallBackQueryExerciseName(String callBackData) {
        for (ExerciseName value : ExerciseName.values()) {
            if (callBackData.equals(value.name())) {
                return true;
            }
        }
        return false;
    }
}



