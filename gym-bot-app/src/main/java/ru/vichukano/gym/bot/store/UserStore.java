package ru.vichukano.gym.bot.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.store.listener.UserRemovalListener;

import java.time.Duration;

public enum UserStore {
    USER_STORE;
    public final Cache<String, User> USERS = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(30))
            .removalListener(new UserRemovalListener())
            .build();
}
