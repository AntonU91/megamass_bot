package com.anton.uzhva.megamazz_bot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void whenUserSetArbitraryDefaultExrcListThanDefaultExrcListMustBeEqualExerciseListUserGet() {
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

}