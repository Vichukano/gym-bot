package ru.vichukano.gym.bot.domain;

public enum State {
    START_TRAINING("training"),
    SELECT_EXERCISE("exercise"),
    SELECT_WEIGHT("weight"),
    SELECT_REPS("reps"),
    STOP("stop");
    private final String name;

    State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
