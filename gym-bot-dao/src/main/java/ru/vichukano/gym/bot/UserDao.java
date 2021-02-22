package ru.vichukano.gym.bot;

import ru.vichukano.gym.bot.model.SavedUser;

import java.io.File;
import java.util.Optional;

public interface UserDao {

    void saveOrUpdate(SavedUser user);

    Optional<File> getByFileName(String fileName);

}
