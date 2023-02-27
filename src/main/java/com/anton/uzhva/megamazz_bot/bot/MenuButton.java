package com.anton.uzhva.megamazz_bot.bot;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.menubutton.MenuButtonCommands;

import java.util.ArrayList;
import java.util.List;

public class MenuButton {
    public final static List<BotCommand> botCommands = new ArrayList<>();

    static {
        botCommands.add(new BotCommand("/info", "information about bot"));
        botCommands.add(new BotCommand("/getresult", "get training result"));
    }
}
