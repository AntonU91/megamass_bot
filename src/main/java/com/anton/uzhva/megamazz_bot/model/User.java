package com.anton.uzhva.megamazz_bot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Entity(name = "user")
@Component
@Getter
@Setter
public class User {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "user_login")
    private String userLogin;

    @Transient
    private static final ArrayList<String> DEFAULT_EXERCISES = new ArrayList<>(
            Arrays.asList("Bench press", "Squats with barbell", "Deadlift"));

    @Column(name = "user_exercises", nullable = false)
    private String exercises;

    @Transient
    private ArrayList<String> userExercises = new ArrayList<>(DEFAULT_EXERCISES);

    private String createExersises(List<String> exercises) {
        String str = "";
        for (int i = 0; i < exercises.size(); i++) {
            if (i == exercises.size() - 1) {
                str += exercises.get(i);
            } else {
                str += exercises.get(i) + ", ";
            }
        }
        return str;
    }

    public void setDefaultExercises() {
        exercises = createExersises(DEFAULT_EXERCISES);
    }

    public void addExercise(String newExercise) {
        exercises += newExercise + ", ";
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + id +
                ", firstName='" + firstName + '\'' +
                ", userName='" + userLogin + '\'' +
                '}';
    }

}
