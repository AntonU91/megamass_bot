package com.anton.uzhva.megamazz_bot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Getter;
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
    public static final List<String> DEFAULT_EXERCISES = new ArrayList<>(
            Arrays.asList("Bench press", "Squats with barbell", "Deadlift"));

    @Column(name = "user_exercises", nullable = false)
    private String exercises;

    @Transient
    private ArrayList<String> userExercises = new ArrayList<>(DEFAULT_EXERCISES);

    private String createDefaultExercises(List<String> exercises) {
        StringBuilder exercisesStr = new StringBuilder();
        for (String exercise : exercises) {
            exercisesStr.append(exercise)
                    .append(",")
                    .append(" ");
        }
        return exercisesStr.toString();
    }

    public void setDefaultExercises(List<String> defaultExercises) {
        exercises = createDefaultExercises(defaultExercises);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(firstName, user.firstName) && Objects.equals(userLogin, user.userLogin) && Objects.equals(exercises, user.exercises) && Objects.equals(userExercises, user.userExercises);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, userLogin, exercises, userExercises);
    }
}
