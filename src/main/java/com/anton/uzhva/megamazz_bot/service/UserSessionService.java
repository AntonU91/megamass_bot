package com.anton.uzhva.megamazz_bot.service;

import com.anton.uzhva.megamazz_bot.model.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserSessionService {
    Map<Long, UserSession> userSessionMap =  new HashMap<>();

    public UserSession getSession (long chatId) {
        return  userSessionMap.getOrDefault(chatId, UserSession.builder()
                                            .chatId(chatId)
                                            .build());
    }
    public void  saveUserSession (long chatId, UserSession userSession) {
        userSessionMap.put(chatId, userSession);
    }


}
