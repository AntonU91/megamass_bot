package com.anton.uzhva.megamazz_bot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void whenUserSetArbitraryDefaultExrcListThanDefaultExrcListMustBeEqualToExerciseListUserGet() {
        ArrayList<String> DEFAULT_EXERCISES = new ArrayList<>(
                Arrays.asList("Bench press", "Squats with barbell", "Deadlift"));
        user.setDefaultExercises(DEFAULT_EXERCISES);
        assertArrayEquals(DEFAULT_EXERCISES.toArray(), user.getUserExercises().toArray());
    }

    @Test
    void whenUserAddedNewExerciseThanStringThatContainExercisesMustBeCorrect() {
        String exercises = "FirstExr, SecondExr, ThirdExr, ";
        user.setExercises(exercises);
        user.addExercise("FourthExrc");
        assertEquals("FirstExr, SecondExr, ThirdExr, FourthExrc, ", user.getExercises());
    }

    @Test
    void whenCreatedInitialExercisesFieldThanStringContainExercisesMustHaveCorrectValue() {
        try {
            List<String> exercises = new ArrayList<>(
                    Arrays.asList("Bench press", "Squats with barbell", "Deadlift"));

            Method method = User.class.getDeclaredMethod("createDefaultExercises", List.class);
            method.setAccessible(true);
            String exercisesStr = (String) method.invoke(user, exercises);
            assertEquals("Bench press, Squats with barbell, Deadlift, ", exercisesStr);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }



}