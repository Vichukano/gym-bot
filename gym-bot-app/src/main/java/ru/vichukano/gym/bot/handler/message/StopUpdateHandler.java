package ru.vichukano.gym.bot.handler.message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.vichukano.gym.bot.domain.Command.STOP;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.text;
import static ru.vichukano.gym.bot.util.MessageUtils.userId;

@Slf4j
@AllArgsConstructor
public class StopUpdateHandler extends AbstractUpdateHandler {
    private final UserService service;

    @Override
    public SendMessage handle(Update message) {
        var out = super.handle(message);
        String text = text(message);
        if (STOP.getCommand().equals(text)) {
            User user = USER_STORE.USERS.asMap().get(userId(message));
            Training training = user.getTraining();
            out.setText("Stop training. Your results:\n"
                    + "Training session time: "
                    + Duration.between(training.getTime(), LocalDateTime.now()).toMinutes()
                    + " minutes"
                    + "\nExercises:\n"
                    + training.getExercises().stream().map(Objects::toString).collect(Collectors.joining("\n"))
                    + "\ntype "
                    + Command.REPORT.getCommand()
                    + " for send training report."
            );
            service.saveUserTrainingInfo(user);
            USER_STORE.USERS.asMap().remove(user.getId());
            log().debug("Remove user form cache: {}", user);
        } else {
            out.setText("Something goes wrong...");
        }
        return out;
    }

    @Override
    protected Logger log() {
        return log;
    }
}
