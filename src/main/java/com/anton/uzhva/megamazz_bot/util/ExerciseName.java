package com.anton.uzhva.megamazz_bot.util;

public enum ExerciseName {
    PUSH("Жим"),
    SQUAT("Присед"),
    DEAD_LIFT("Становая тяга"),
    BENCH_PRESS("Жим под наклоном"),
    LUNGE("Выпады");

    final String exrcsName;

    ExerciseName(String exrcsName) {
        this.exrcsName =  exrcsName;
    }

    public String getExrcsName() {
        return exrcsName;
    }
}
