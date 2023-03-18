package com.anton.uzhva.megamazz_bot.handler.commands_handler;

import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.helper.KeyboardHelper;
import com.anton.uzhva.megamazz_bot.model.BodyWeight;
import com.anton.uzhva.megamazz_bot.model.ConversationState;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import com.anton.uzhva.megamazz_bot.model.UserSession;
import com.anton.uzhva.megamazz_bot.service.BodyWeightService;
import com.anton.uzhva.megamazz_bot.service.TelegramService;
import com.anton.uzhva.megamazz_bot.service.UserSessionService;
import com.anton.uzhva.megamazz_bot.util.Period;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class DisplayBodyWeightRecordsForSpecifiedPeriodHandler extends UserRequestHandler {
    BodyWeightService bodyWeightService;
    KeyboardHelper keyboardHelper;
    TelegramService telegramService;
    UserSessionService userSessionService;

    @Override
    public boolean isApplicable(UserRequest request) {
        return request.getSession().getState().equals(ConversationState.BODY_WEIGHT_OPTION)
                && isMessageTextMatchSelectionPeriod(request.getUpdate());
    }

    @Override
    public void handle(UserRequest request) {
        UserSession userSession = userSessionService.getSession(request.getChatId());
        String text = request.getUpdate().getMessage().getText();
        Period period = getPeriod(request.getUpdate());
        List<BodyWeight> bodyWeightList = bodyWeightService.getResultsOfSpecifiedDiapason(request.getChatId(), period);
        for (BodyWeight bodyWeight : bodyWeightList) {
            System.out.println(bodyWeight);
        }
        // TODO Complete fully and check
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    private Period getPeriod(Update update) {
        String messageText = update.getMessage().getText();
        for (Period value : Period.values()) {
            if (messageText.equals(value.getName())) {
                return value;
            }
        }
        throw new NoSuchElementException();
    }

    private boolean isMessageTextMatchSelectionPeriod(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            for (Period value : Period.values()) {
                if (messageText.equals(value.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
