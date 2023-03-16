package com.anton.uzhva.megamazz_bot.util;

import lombok.Getter;

@Getter
public enum Periods {

    ONE_WEEK(7, "Last week"),
    ONE_MONTH(31, "Last month"),
    THREE_MONTHS(91, "Last three months"),
//    HALF_YEAR(182),
//    ONE_YEAR(365);
;
    final int days;
    final String name;

    Periods(int days, String name) {
        this.days = days;
        this.name = name;
    }

    }
