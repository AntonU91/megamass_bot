package com.anton.uzhva.megamazz_bot.util;

import lombok.Getter;

@Getter
public enum Period {
    ONE_WEEK(7, "Last week"),
    TWO_WEEK(14, "Last two weeks"),
    ONE_MONTH(31, "Last month"),
    THREE_MONTHS(91, "Last three months"),
    HALF_YEAR(182, "Last half an year"),
    ONE_YEAR(365, "Last year"),
    LAST_RESULT(0, "Last result"),
    WHOLE_PERIOD(0, "All results");

    final int days;
    final String name;

    Period(int days, String name) {
        this.days = days;
        this.name = name;
    }

}
