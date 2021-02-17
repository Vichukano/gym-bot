package ru.vichukano.gym.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Exercise {
    BENCH_PRESS("/bench"),
    SQUAT("/squat"),
    DEAD_LIFT("/dead_lift");

    @Getter
    private final String command;
}
