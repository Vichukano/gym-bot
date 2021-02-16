package ru.vichukano.gym.bot.store;

import ru.vichukano.gym.bot.domain.State;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UserStateStore {
    STATE_STORE;
    public final Map<String, State> STORE = new ConcurrentHashMap<>();
}
