package com.anton.uzhva.megamazz_bot.constant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true)
public class Constants {
  public static String REGEX_USER_LOGIN = "^\\s*\\D+.*";
  public  static  String START_COMMAND = "/start";

}
