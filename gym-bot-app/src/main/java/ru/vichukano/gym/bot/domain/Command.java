package ru.vichukano.gym.bot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Command {
    START("/start"),
    STOP("/stop"),
    REPORT("/report");

    @Getter
    private final String command;
}
