package com.anton.uzhva.megamazz_bot.util;

import com.anton.uzhva.megamazz_bot.service.UserService;

public interface UserRegistrationChecker {

    static boolean  isUserRegistered(UserService userService, long chatId) {
       return userService.findUserById(chatId).isPresent();
   }
}
