package com.anton.uzhva.megamazz_bot.service;

import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.sender.MegamassBotSender;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class TelegramService {
    final MegamassBotSender botSender;


    @Autowired
    public TelegramService(MegamassBotSender botSender) {
        this.botSender = botSender;
    }

    public void sendMessage(long chatId, String msgText) {
        SendMessage sendMsg = SendMessage.builder()
                .text(msgText)
                .chatId(chatId)
                .build();
        execute(sendMsg);

    }
    public void sendMessage(long chatId, String msgText, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMsg = SendMessage.builder()
                .text(msgText)
                .chatId(chatId)
                .replyMarkup(replyKeyboardMarkup)
                .build();
        execute(sendMsg);

    }
    public void sendMessage(long chatId, String msgText, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMsg = SendMessage.builder()
                .text(msgText)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(sendMsg);
    }

    private void execute (BotApiMethod<?> botApiMethod) {
        try {
            botSender.execute(botApiMethod);
        } catch (TelegramApiException e) {
          log.error("Some problem with executing "+ e.getMessage());
        }
    }
}
