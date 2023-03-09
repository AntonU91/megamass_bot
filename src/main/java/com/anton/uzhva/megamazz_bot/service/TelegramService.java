package com.anton.uzhva.megamazz_bot.service;

import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.Exercise;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
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


    public void editMessage(Update update, String msgText, InlineKeyboardMarkup inlineKeyboardMarkup) {
        long chatId;
        int messageId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            messageId = update.getMessage().getMessageId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .text(msgText)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(editMessageText);
    }

    private void execute(BotApiMethod<?> botApiMethod) {
        try {
            botSender.execute(botApiMethod);
        } catch (TelegramApiException e) {
            log.error("Some problem with executing " + e.getMessage());
        }
    }
}
