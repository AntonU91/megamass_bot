package com.anton.uzhva.megamazz_bot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;

@Entity(name = "user")
@Component
public class User {
    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "user_login")
    private String userLogin;

    @Transient
    private static final ArrayList<String> DEFAULT_EXERCISES = new ArrayList<>(
            Arrays.asList("ABC", "DEF", "GKL", "XYZ"));
    // new ArrayList<>(
    // Arrays.asList("Жим", "Присяд", "Становая тяга", "Жим под наклоном"));

    @Column(name = "user_exercises", nullable = false)
    private String exercises;

    @Transient
    private ArrayList<String> userExercises = new ArrayList<>(DEFAULT_EXERCISES);

    private  String createExersises(List<String> exercises) {
        String str = new String();
        for (int i = 0; i < exercises.size(); i++) {
            if (i == exercises.size() - 1) {
                str += exercises.get(i);
            } else {
                str += exercises.get(i) + ", ";
            }
        }
        return str;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userName) {
        this.userLogin = userName;
    }

    public Long getId() {
        return chatId;
    }

    public void setId(Long id) {
        this.chatId = id;
    }

    // public List<String> getExerciseList() {
    // return userExercises;
    // }

    public void setDefaultExercises() {
        exercises = createExersises(DEFAULT_EXERCISES);
    }

    public void addExercise(String newExercise) {
        userExercises.add(newExercise);
        exercises = createExersises(userExercises);
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", userName='" + userLogin + '\'' +
                '}';
    }

    public String getExercises() {
        return exercises;
    }

    public void setExercises(String exercises) {
        this.exercises = exercises;
    }
}
