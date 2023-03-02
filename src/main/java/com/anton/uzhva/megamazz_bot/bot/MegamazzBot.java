package com.anton.uzhva.megamazz_bot.bot;

import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.ExerciseRepo;
import com.anton.uzhva.megamazz_bot.model.User;
import com.anton.uzhva.megamazz_bot.model.UserRepo;
import com.anton.uzhva.megamazz_bot.service.ExerciseSevice;
import com.anton.uzhva.megamazz_bot.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.NoResultException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


@Component
@Slf4j

// TODO Find out aproach how to delete file with training results from project directory
/*
cancel - cancel current action
getresult - show trainings result records
getresultsfile- create txt file with trainings results records
deleteresults - delete all trainings results
*/

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
    private String exerciseToDelete;
    private int fileCounter = 1;
    @Autowired
    Exercise currentExerciseRecord;


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
            log.error("Error of processing message " + e.getMessage());
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
        } else if (msg.equals("/getresultsfile")) {
            try {
                execute(createFileAndWriteThereAllRecords(chatId));
            } catch (TelegramApiException | IOException e) {
                log.error("Problem with file creating " + e.getMessage());
            }
        } else if (msg.matches("/getresult")) {
            executeMsg(getListOfTrainingWeeks(update));
        } else if (msg.matches(("/cancel"))) {
            executeMsg(acceptResultIndicators(update));
        } else if (msg.equals("/deleteresults")) {
            executeMsg(deleteAllTrainingResult(chatId));
        } else if (isPassedMessageTextExerciseName(msg, chatId)) {
            executeMsg(notifyThatExerciseWasDeleted(msg, chatId));
            userService.deleteSpecifiedExerciseByUserID(msg, chatId);
        } else if (msg.matches("^\\s*\\D+.*")
                & chekingText.equals("Введите название нового упражнения. Название должно начинаться с буквы")) {
            if (hasUserCreatedLogin(chatId)) {
                executeMsg(saveNewExercise(update));
            } else {
                registration(chatId, update);
            }
            chekingText = null;
        } else if (msg.matches("\\s*\\d{1,3}") &
                chekingText.equals("Введи максимальный весовой результат с клавиатуры")) {
            saveWeightValue(msg);
            executeMsg(selectCountExerciseRepeating(chatId));
            chekingText = null;
        }
    }

    public void processingCallBackQuery(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callBackQueryData = update.getCallbackQuery().getData();
        if (callBackQueryData.equals("INSERT_RESULT")) {
            executeMsg(prepareForExerciseSelection(update));
        } else if (callBackQueryData.equals("GET_RESULT")) {
            executeMsg(getListOfTrainingWeeks(update));

        } else if (isCallBackQueryDataExerciseName(callBackQueryData, chatId)) {
            executeEditMsgText(createResultRecordAndPrepareForGettingValues(update, callBackQueryData));
        } else if (callBackQueryData.matches("\\d{1,3}")) {
            saveCountValue(update);
            saveExcerciseResult(update);
            executeEditMsgText(showExerciseResultAfterInputingDates(update));
        } else if (callBackQueryData.equals("OK")) {
            executeMsg(acceptResultIndicators(update));
        } else if (callBackQueryData.matches("EDIT")) {
            executeMsg(editResultValue(update));
        } else if (callBackQueryData.matches("WEEK-\\d{1,3}")) {
            executeMsg(getTrainingResult(update));
        } else if (callBackQueryData.matches("NEW_EXERCISE")) {
            executeMsg(addNewExercise(chatId));
        } else if (callBackQueryData.matches("DELETE_EXERCISE")) {
            executeMsg(deleteExercise(chatId));
        } else if (callBackQueryData.matches("CANCEL")) {
            executeMsg(acceptResultIndicators(update));
        } else if (callBackQueryData.equals("DELETE_RESULTS")) {
            exerciseService.deleteAllUserTrainingsResults(chatId);
            executeMsg(acceptResultIndicators(update));
        }
    }

    public InlineKeyboardMarkup selectExerciseKeyBoard(long chatId) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<String> exerciseNames = userService.getExerciseList(chatId);
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
        InlineKeyboardButton buttonToDeleteExrcs = new InlineKeyboardButton();
        buttonForAddNewExrcs.setText("Добавить упражнение");
        buttonForAddNewExrcs.setCallbackData("NEW_EXERCISE");
        buttonToDeleteExrcs.setText("Удалить упражнение");
        buttonToDeleteExrcs.setCallbackData("DELETE_EXERCISE");
        List<InlineKeyboardButton> rowForDeletingAndAdding = new ArrayList<>();
        rowForDeletingAndAdding.add(buttonForAddNewExrcs);
        rowForDeletingAndAdding.add(buttonToDeleteExrcs);
        rowList.add(rowForDeletingAndAdding);
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
        currentExerciseRecord.setName(callBackQueryData); //
        Date recordingDate = new Date();
        currentExerciseRecord.setRecordDate(recordingDate);
        if (exerciseService.findAtLeastOneExerciceRecordByUserId(chatId).isEmpty()) {
            currentExerciseRecord.setWeekNumber(1);
        } else {
            currentExerciseRecord.setWeekNumber(defineTheWeeksOfTraining(currentExerciseRecord.getRecordDate(), chatId));
        }
        chekingText = "Введи максимальный весовой результат с клавиатуры";
        messageText.setReplyMarkup(cancelAction());
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
        if (update.hasCallbackQuery()) {
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        } else sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }

    public void saveWeightValue(String msg) {
        currentExerciseRecord.setWeight(Double.parseDouble(msg));
    }

    public void saveCountValue(Update update) {
        int count = Integer.parseInt(update.getCallbackQuery().getData());
        currentExerciseRecord.setCount(count);

    }

    private void saveExcerciseResult(Update update) {
        User user = userRepo.findById(update.getCallbackQuery().getMessage().getChatId()).get();
        currentExerciseRecord.setUser(user);
        exerciseRepo.save(currentExerciseRecord);
        Exercise retrievedResult = (Exercise) exerciseService.findExerciseByRecordDate(currentExerciseRecord.getRecordDate());
        resultId = retrievedResult.getId();
        currentExerciseRecord = new Exercise();
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

    boolean isCallBackQueryDataExerciseName(String callBackData, long chatId) {
        for (String exerciseList : userService.getExerciseList(chatId)) {

            if (callBackData.equals(exerciseList)) {
                return true;
            }
        }
        return false;
    }

    boolean isPassedMessageTextExerciseName(String message, long chatId) {
        for (String exercise : userService.getExerciseList(chatId)) {
            if (message.matches("\\s*" + exercise)) {
                return true;
            }
        }
        return false;
    }


    private SendMessage getListOfTrainingWeeks(Update update) {
        long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
        } else chatId = update.getCallbackQuery().getMessage().getChatId();

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

    private int defineTheWeeksOfTraining(Date date, long chatId) {
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
        List<Exercise> exercisesResult = exerciseService.getTrainingResultOfConcreteWeek(chatId, weekNumber);
        results.append("Тренировочная неделя №").append(weekNumber).append("\n").append("\n");
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
        message.setReplyMarkup(cancelAction());
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
        message.setReplyMarkup(acceptInfo());
        return message;
    }

    private SendMessage deleteExercise(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите упражнение, которое нужно удалить внизу екрана");
        message.setReplyMarkup(listOfExercisesToDelete(chatId));
        return message;

    }

    private ReplyKeyboardMarkup listOfExercisesToDelete(long chatId) {
        List<String> exerciseList = userService.getExerciseList(chatId);
        KeyboardRow keyboardRow = new KeyboardRow();
        List<KeyboardRow> rowList = new ArrayList<>();

        for (int i = 0; i < exerciseList.size(); i++) {
            KeyboardButton button = new KeyboardButton();
            button.setText(exerciseList.get(i));
            keyboardRow.add(button);
            if ((i + 1) % 2 == 0) {
                rowList.add(keyboardRow);
                keyboardRow = new KeyboardRow();
            }
            if (i + 1 == exerciseList.size()) {
                rowList.add(keyboardRow);
            }
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    private SendMessage notifyThatExerciseWasDeleted(String messageToDelete, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(String.format("Упражнение \"%s\" удалено ", messageToDelete));
        message.setReplyMarkup(acceptInfo());
        return message;
    }

    private InlineKeyboardMarkup cancelAction() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton OK_Button = new InlineKeyboardButton();
        OK_Button.setText("Cancel");
        OK_Button.setCallbackData("CANCEL");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(OK_Button);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private SendDocument createFileAndWriteThereAllRecords(long chatId) throws IOException {
        List<Exercise> exerciseList = exerciseService.getAllTrainingsResults(chatId);
        java.io.File file = new File("results" + fileCounter + ".txt");
        FileWriter writer = new FileWriter(file);
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 0;
        for (Exercise exercise : exerciseList) {
            if (exercise.getWeekNumber() > counter) {
                counter = exercise.getWeekNumber();
                stringBuilder.append("\n")
                        .append(exercise.getWeekNumber())
                        .append(" тренировочная неделя")
                        .append("\n");
            }
            stringBuilder.append(exercise.getName())
                    .append(" , ")
                    .append(exercise.getWeight())
                    .append(" кг на ")
                    .append(exercise.getCount()).append(" раз, ")
                    .append(exercise.getWeekNumber())
                    .append(" неделя")
                    .append("\n");
        }
        writer.write(stringBuilder.toString());
        writer.close();
        InputFile inputFile = new InputFile(file);
        SendDocument document = new SendDocument();
        document.setChatId(chatId);
        document.setDocument(inputFile);
        return document;
    }

    private SendMessage deleteAllTrainingResult(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Ви впевнені, що хочете видалити всі записи результатів тренувань?\n" +
                "Рекомендую зберегти результати перед видаленням командою /getresultsfile");
        message.setReplyMarkup(acceptOrCancel());
        return message;
    }

    public InlineKeyboardMarkup acceptOrCancel() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSeeResults = new InlineKeyboardButton();
        InlineKeyboardButton buttonInsertNewResults = new InlineKeyboardButton();
        buttonSeeResults.setText("Так");
        buttonSeeResults.setCallbackData("DELETE_RESULTS");
        buttonInsertNewResults.setText("Ні");
        buttonInsertNewResults.setCallbackData("CANCEL");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(buttonSeeResults);
        row.add(buttonInsertNewResults);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

}
