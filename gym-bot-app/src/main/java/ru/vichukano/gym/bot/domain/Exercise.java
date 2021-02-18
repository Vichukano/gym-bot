package ru.vichukano.gym.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum Exercise {
    BENCH_PRESS("/bench"),
    SQUAT("/squat"),
    DEAD_LIFT("/dead_lift");

    @Getter
    private final String command;

    public static String printAll() {
        return Arrays.stream(Exercise.values())
                .map(Exercise::getCommand)
                .collect(Collectors.joining(", "));
    }
}
