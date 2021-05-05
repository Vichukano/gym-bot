package ru.vichukano.gym.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Command {
    HELP("/help"),
    START("/start"),
    CANCEL("/cancel"),
    EXERCISE("/exercise"),
    STOP("/stop"),
    REPORT("/report");

    @Getter
    private final String command;
}
