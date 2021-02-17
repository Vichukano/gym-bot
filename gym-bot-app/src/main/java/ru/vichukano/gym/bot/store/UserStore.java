package ru.vichukano.gym.bot.store;

import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.User;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

//TODO: Change for cache
public enum UserStore {
    USER_STORE;
    public final Map<String, State> STATES = new ConcurrentHashMap<>();
    public final Map<String, User> USERS = new ConcurrentHashMap<>();
}
