package ru.vichukano.gym.bot.service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.vichukano.gym.bot.UserDao;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.model.Exercise;
import ru.vichukano.gym.bot.model.SavedUser;
import ru.vichukano.gym.bot.model.Training;

@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public void saveUserTrainingInfo(User user) {
        log.debug("Save training info for: {}", user);
        List<Exercise> exercises = user.getTraining().getExercises().stream()
                .map(exc -> new Exercise(exc.getName(), exc.getWeights(), exc.getReps()))
                .collect(Collectors.toList());
        var training = new Training(user.getTraining().getTime(), exercises);
        SavedUser forSave = new SavedUser(user.getId(), user.getName(), List.of(training));
        userDao.saveOrUpdate(forSave);
        log.trace("Successfully save user: {}", forSave);
    }

    public File getTrainingInfo(User user) {
        log.debug("Get training info for: {}", user);
        String fileName = user.getId() + user.getName();
        Optional<File> fromDao = userDao.getByFileName(fileName);
        if (fromDao.isEmpty()) {
            throw new IllegalStateException("File with name " + fileName + " is absent!");
        }
        return fromDao.get();
    }
}
