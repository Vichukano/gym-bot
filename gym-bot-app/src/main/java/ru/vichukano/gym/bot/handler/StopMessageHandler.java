package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.vichukano.gym.bot.domain.Command.STOP;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class StopMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
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
            );
            user.setState(State.START_TRAINING);
        } else {
            out.setText("Something goes wrong...");
        }
        return out;
    }

}
