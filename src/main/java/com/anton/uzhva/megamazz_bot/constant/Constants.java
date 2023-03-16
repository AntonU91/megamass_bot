package com.anton.uzhva.megamazz_bot.constant;

import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true)
public abstract class Constants {
    public static final String BODY_WEIGHT = "Body weight";
    public static final String ADD_BODY_WEIGHT_VALUE = "Add new body weight value";
    public static final String GET_BODY_WEIGHT_VALUES = "Watch body weight records";
    public static String REGEX_USER_LOGIN = "^\\s*\\D+.*";
    public static String ADD_NEW_RESULT = "ADD_RESULT";
    public static String REGEX_INPUTTED_WEIGHT = "\\s*\\d+[.,]?\\d*";
    public static String REGEX_REPEATING_COUNT = "\\d{1,2}";
    public static String OK = "OK";
    public static String EDIT_RESULT = "EDIT";
    public static String GET_RESULTS = "GET_RESULTS";
    public static String REGEX_TRAINING_WEEK = "WEEK-\\d{1,3}";
    public static String ADD_NEW_EXERCISE = "Add exercise";
    public static String DELETE_EXERCISE = "Delete exercise";
    public static String REGEX_NEW_EXERCISE = "^\\s*\\D+.*";
    public static String DELETE_ALL_RESULTS = "DELETE_RESULTS";
    public static String CANCEL = "CANCEL";
}
