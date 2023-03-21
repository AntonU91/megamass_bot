package com.anton.uzhva.megamazz_bot.service;

import com.anton.uzhva.megamazz_bot.sender.MegamassBotSender;
import com.anton.uzhva.megamazz_bot.util.FileHandler;
import com.anton.uzhva.megamazz_bot.util.FileRemover;
import com.anton.uzhva.megamazz_bot.util.FileToDeleteContainer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class TelegramService {
    final MegamassBotSender botSender;
    final FileHandler fileHandler;
    final FileToDeleteContainer fileToDeleteContainer;
    final  FileRemover fileRemover;

    public TelegramService(MegamassBotSender botSender, FileHandler fileHandler, FileToDeleteContainer fileToDeleteContainer) {
        this.botSender = botSender;
        this.fileHandler = fileHandler;
        this.fileToDeleteContainer = fileToDeleteContainer;
        this.fileRemover =  new FileRemover(fileToDeleteContainer);
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

    public void sendTXTFileWithResults(Update update, InlineKeyboardMarkup inlineKeyboardMarkup) {
        long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
        } else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        File fileToSend = fileHandler.createFileWithTraningResults(chatId);
        InputFile inputFile = new InputFile(fileToSend);
        SendDocument sendDocument = SendDocument.builder()
                .document(inputFile)
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(chatId)
                .build();
        try {
            botSender.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        fileToDeleteContainer.addFileToContainer(fileToSend);
    }

    private void execute(BotApiMethod<?> botApiMethod) {
        try {
            botSender.execute(botApiMethod);
        } catch (TelegramApiException e) {
            log.error("Some problem with executing " + e.getMessage());
        }
    }
}
