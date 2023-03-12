package com.anton.uzhva.megamazz_bot.util;

import com.anton.uzhva.megamazz_bot.model.Exercise;
import com.anton.uzhva.megamazz_bot.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Component
public class FileHandler {

    public final ExerciseService exerciseService;

    @Autowired
    public FileHandler(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    public File getFileWithTraningResults(long chatId) {
        String currentDate = new Date().toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        File file = null;
        try {
            file = new File("results_" + simpleDateFormat.parse(currentDate));
            fillOutFileWithTrainingResults(chatId, file);
            return file;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void fillOutFileWithTrainingResults(long chatId, File file) {
        List<Exercise> exerciseList = exerciseService.findAllExerciseRecordByUserId(chatId);
        try (FileWriter writer = new FileWriter(file)) {
            StringBuilder stringBuilder = new StringBuilder();
            int counter = 0;
            for (Exercise exercise : exerciseList) {
                if (exercise.getWeekNumber() > counter) {
                    counter = exercise.getWeekNumber();
                    stringBuilder.append("\n")
                            .append(exercise.getWeekNumber())
                            .append(" training week")
                            .append("\n");
                }
                stringBuilder.append(exercise.getName())
                        .append(" , ")
                        .append(exercise.getWeight())
                        .append(" kg for ")
                        .append(exercise.getCount()).append(" time(s), ")
                        .append(exercise.getWeekNumber())
                        .append(" week")
                        .append("\n");
            }
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
