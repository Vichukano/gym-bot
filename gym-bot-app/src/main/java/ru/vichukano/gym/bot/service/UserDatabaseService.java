package ru.vichukano.gym.bot.service;

import lombok.extern.slf4j.Slf4j;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;
import ru.vichukano.gym.bot.domain.dto.User;

@Slf4j
public class UserDatabaseService {
    private final DatabaseConnection con;

    public UserDatabaseService(DatabaseConnection con) {
        this.con = con;
    }

    public void saveUserInfo(User user) {
        throw new UnsupportedOperationException("Not ready yet");
    }
}
