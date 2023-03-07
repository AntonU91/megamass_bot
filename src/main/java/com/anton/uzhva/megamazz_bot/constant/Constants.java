package com.anton.uzhva.megamazz_bot.constant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true)
public class Constants {
  public static String REGEX_USER_LOGIN = "^\\s*\\D+.*";
  public  static  String START_COMMAND = "/start";
  public  static  String ADD_NEW_RESULT = "ADD_RESULT";
  public  static  String REGEX_INPUTED_WEIGHT = "\\s*\\d+[.,]?\\d*";
}
