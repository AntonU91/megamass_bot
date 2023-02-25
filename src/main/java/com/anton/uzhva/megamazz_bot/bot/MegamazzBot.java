package com.anton.uzhva.megamazz_bot.bot;

import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.ExerciseRepo;
import com.anton.uzhva.megamazz_bot.model.User;
import com.anton.uzhva.megamazz_bot.model.UserRepo;
import com.anton.uzhva.megamazz_bot.service.ExerciseSevice;
import com.anton.uzhva.megamazz_bot.service.UserService;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

@Component
public class MegamazzBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUserName;
    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ExerciseRepo exerciseRepo;

    @Autowired
    private ExerciseSevice exerciseService;

    @Autowired
    private UserService userService;

    @Autowired
    private User user;

    private Long resultId = 0L;

    private String chekingText;

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
            divider(userMsg, update);
        } else if (update.hasCallbackQuery()) {
            processingCallBackQuery(update);
        }
    }

    private void executeEditMsgText(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void executeMsg(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage greetingToUnregisteredUser(Long chatId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(EmojiParser.parseToUnicode(String.format("Привет, %s. Придумай свой логин:muscle:" +
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
            user.setDefaultExercises();
            userRepo.save(user);
            executeMsg(greetingToExistUser(chatId, userLogin));
        }
    }

    public void divider(String msg, Update update) {

        long chatId = update.getMessage().getChatId();
        if (msg.matches("/start")) {
            Optional<User> user = userRepo.findById(update.getMessage().getChatId());
            if (user.isPresent()) {
                String userLogin = user.get().getUserLogin();
                executeMsg(greetingToExistUser(chatId, userLogin));
            } else {
                executeMsg(greetingToUnregisteredUser(chatId, update));
            }
        } else if (msg.matches("^\\s*\\D+.*")
                & chekingText.equals("Введите название нового упражнения. Название должно начинаться с буквы")) {
            if (hasUserCreatedLogin(chatId)) {
                executeMsg(saveNewExercise(update));
            } else {
                registration(chatId, update);
            }
            chekingText = null;
        } else if (msg.matches("\\s*\\d{1,3}.*") &
                chekingText.equals("Введи максимальный весовой результат с клавиатуры")) {
            saveWeightValue(msg);
            executeMsg(selectCountExerciseRepeating(chatId));
            chekingText = null;
        }
    }

    public InlineKeyboardMarkup selectExerciseKeyBoard(long chatId) { // TODO: change the methods details related to
                                                                      // geting
        // exersices from DB and transforming it.
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<String> exerciseNames = userService.getExerciseList(chatId); // тут нужно вытягивать лист конкретного
                                                                          // Entity и делать это через userService
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (int i = 0; i < exerciseNames.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(exerciseNames.get(i));
            button.setCallbackData(exerciseNames.get(i));
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rowList.add(row);
                row = new ArrayList<>();
            }
            if (i + 1 == exerciseNames.size()) {
                rowList.add(row);
            }
        }
        InlineKeyboardButton buttonForAddNewExrcs = new InlineKeyboardButton();
        buttonForAddNewExrcs.setText("Добавить новое упражнение");
        buttonForAddNewExrcs.setCallbackData("NEW_EXERCISE");
        row.add(buttonForAddNewExrcs);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
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

    public void processingCallBackQuery(Update update) { // TODO change some methods parameter => Update update to long
                                                         // chatId
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callBackQueryData = update.getCallbackQuery().getData();
        if (callBackQueryData.equals("INSERT_RESULT")) {
            executeMsg(prepareForExerciseSelection(update));
        } else if (callBackQueryData.equals("GET_RESULT")) {
            executeMsg(getListOfTrainingWeeks(update));

        } else if (isCallBackQueryExerciseName(callBackQueryData, chatId)) {
            executeEditMsgText(createResultRecordAndPrepareForGettingValues(update, callBackQueryData));
        } else if (callBackQueryData.matches("\\d{1,3}")) {
            saveCountValue(update);
            executeEditMsgText(showExerciseResultAfterInputingDates(update));

        } else if (callBackQueryData.equals("OK")) {
            executeMsg(acceptResultIndicators(update));

        } else if (callBackQueryData.matches("EDIT")) {
            executeMsg(editResultValue(update));
        } else if (callBackQueryData.matches("WEEK-\\d{1,3}")) {
            executeMsg(getTrainingResult(update));
        } else if (callBackQueryData.matches("NEW_EXERCISE")) {
            executeMsg(addNewExercise(chatId));
        }

    }

    private SendMessage prepareForExerciseSelection(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage messageText = new SendMessage();
        messageText.setText("Выбери категорию");
        messageText.setChatId(chatId);
        messageText.setReplyMarkup(selectExerciseKeyBoard(chatId));
        return messageText;
    }

    private EditMessageText createResultRecordAndPrepareForGettingValues(Update update, String callBackQueryData) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        EditMessageText messageText = new EditMessageText();
        User user = userRepo.findById(chatId).get();
        Exercise exercise = new Exercise();
        exercise.setName(callBackQueryData); //
        exercise.setUser(user);
        Date recordingDate = new Date();
        exercise.setRecordDate(new Date());
        if (exerciseService.findAtLeastOneExerciceRecordByUserId(chatId).isEmpty()) {
            exercise.setWeekNumber(1);
        } else {
            exercise.setWeekNumber(defineTheWeeksOfTrainingProcess(exercise.getRecordDate(), chatId));
        }
        exerciseRepo.save(exercise);

        exercise = (Exercise) exerciseService.findExerciseByRecordDate(recordingDate);
        System.out.println(exercise.getWeight());

        resultId = exercise.getId();
        chekingText = "Введи максимальный весовой результат с клавиатуры";
        messageText.setText(chekingText);
        messageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        messageText.setChatId(chatId);
        return messageText;
    }

    SendMessage editResultValue(Update update) {
        SendMessage messageText = new SendMessage();
        Exercise exercise = exerciseRepo.findById(resultId).get();
        chekingText = "Введи максимальный весовой результат с клавиатуры";
        messageText.setText(
                String.format("Для редактирования упражнения \"%s\" введи максимальный весовой результат с клавиатуры",
                        exercise.getName()));
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

    public EditMessageText showExerciseResultAfterInputingDates(Update update) {
        EditMessageText editMessageText = new EditMessageText();
        Exercise exercise = exerciseRepo.findById(resultId).get();
        editMessageText.setText(String.format("Отлично! Твой результат в упражнение %s - %.1f кг на %d раз",
                exercise.getName(), exercise.getWeight(), exercise.getCount()));
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setChatId(update.getCallbackQuery().getFrom().getId());
        editMessageText.setReplyMarkup(acceptOrChangeResultValue());
        return editMessageText;
    }

    public InlineKeyboardMarkup selector() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
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

    boolean isCallBackQueryExerciseName(String callBackData, long chatId) {
        for (String exerciseList : userService.getExerciseList(chatId)) { // тут нужно вытягивать лист конкретного
                                                                          // Entity и делать это через userService
            if (callBackData.equals(exerciseList)) {
                return true;
            }
        }
        return false;
    }

    private SendMessage getListOfTrainingWeeks(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (exerciseService.findAtLeastOneExerciceRecordByUserId(chatId).isEmpty()) {
            message.setText("У вас пока что нет записанных результатов");
        } else {
            message.setText("Выбeрите тренировочную неделю");
            message.setReplyMarkup(createListOfTrainingWeek(chatId));
        }
        return message;
    }

    private int defineTheWeeksOfTrainingProcess(Date date, long chatId) {
        Exercise exercise = (Exercise) exerciseService.getTheErliestRecord(chatId);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar2.setTime(exercise.getRecordDate());
        int result = gregorianCalendar.get(Calendar.WEEK_OF_YEAR) - gregorianCalendar2.get(Calendar.WEEK_OF_YEAR);
        return result + 1;
    }

    private InlineKeyboardMarkup createListOfTrainingWeek(long chatId) {
        List<Integer> trainingWeekNumberList = exerciseService.getListOfTrainingWeeksNumber(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < trainingWeekNumberList.size(); i++) {
            InlineKeyboardButton countButton = new InlineKeyboardButton();
            countButton.setText(String.valueOf(trainingWeekNumberList.get(i)));
            countButton.setCallbackData("WEEK-" + String.valueOf(trainingWeekNumberList.get(i)));
            row.add(countButton);
            if ((i + 1) % 3 == 0) {
                rowList.add(row);
                row = new ArrayList<>();
            }
            if (i + 1 == trainingWeekNumberList.size()) {
                rowList.add(row);
            }
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private SendMessage getTrainingResult(Update update) {
        SendMessage message = new SendMessage();
        StringBuilder results = new StringBuilder();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer weekNumber = Integer.parseInt(update.getCallbackQuery().getData().replace("WEEK-", ""));
        List<Exercise> exercisesResult = exerciseService.getTrainingResult(chatId, weekNumber);

        results.append("Тренировочная неделя №" + weekNumber + "\n");
        for (Exercise temp : exercisesResult) {
            results.append(String.format("Упражнение %s - %.1f кг на %d раз\n", temp.getName(), temp.getWeight(),
                    temp.getCount()));
        }
        message.setChatId(chatId);
        message.setText(results.toString());
        message.setReplyMarkup(acceptInfo());
        return message;
    }

    private InlineKeyboardMarkup acceptInfo() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton OK_Button = new InlineKeyboardButton();
        OK_Button.setText("Ok");
        OK_Button.setCallbackData("OK");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(OK_Button);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private SendMessage addNewExercise(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        chekingText = "Введите название нового упражнения. Название должно начинаться с буквы";
        message.setText(chekingText);
        return message;
    }

    private boolean hasUserCreatedLogin(long chatId) {
        try {
            userService.getUserLogin(chatId);
        } catch (NoResultException ex) {
            return false;
        }
        return true;
    }

    private SendMessage saveNewExercise(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        String exerciseName = update.getMessage().getText();
        userService.addExercise(chatId, exerciseName);
        message.setChatId(update.getMessage().getChatId());
        message.setText("Вы добавили новое упражнение - " + exerciseName);
        return message;
    }

}
