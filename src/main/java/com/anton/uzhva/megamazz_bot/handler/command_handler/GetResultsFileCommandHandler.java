package com.anton.uzhva.megamazz_bot.handler.command_handler;

import com.anton.uzhva.megamazz_bot.commands.BotCommands;
import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class GetResultsFileCommandHandler extends UserRequestHandler {
    ExerciseService exerciseService;
    TelegramService telegramService;
    UserSessionService userSessionService;

    @Autowired
    public GetResultsFileCommandHandler(ExerciseService exerciseService, TelegramService telegramService, UserSessionService userSessionService) {
        this.exerciseService = exerciseService;
        this.telegramService = telegramService;
        this.userSessionService = userSessionService;
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isCommand(request.getUpdate(), BotCommands.GET_RESULTS_FILE);
    }

    @Override
    public void handle(UserRequest request) {

    }

    @Override
    public boolean isGlobal() {
        return true;
    }


//    private SendDocument createFileAndWriteThereAllRecords(long chatId) throws IOException {
//        List<Exercise> exerciseList = exerciseService.getAllTrainingsResults(chatId);
//        java.io.File file = new File("results" + fileCounter + ".txt");
//        FileWriter writer = new FileWriter(file);
//        StringBuilder stringBuilder = new StringBuilder();
//        int counter = 0;
//        for (Exercise exercise : exerciseList) {
//            if (exercise.getWeekNumber() > counter) {
//                counter = exercise.getWeekNumber();
//                stringBuilder.append("\n")
//                        .append(exercise.getWeekNumber())
//                        .append(" тренировочная неделя")
//                        .append("\n");
//            }
//            stringBuilder.append(exercise.getName())
//                    .append(" , ")
//                    .append(exercise.getWeight())
//                    .append(" кг на ")
//                    .append(exercise.getCount()).append(" раз, ")
//                    .append(exercise.getWeekNumber())
//                    .append(" неделя")
//                    .append("\n");
//        }
//        writer.write(stringBuilder.toString());
//        writer.close();
//        InputFile inputFile = new InputFile(file);
//        SendDocument document = new SendDocument();
//        document.setChatId(chatId);
//        document.setDocument(inputFile);
//        return document;
//    }
