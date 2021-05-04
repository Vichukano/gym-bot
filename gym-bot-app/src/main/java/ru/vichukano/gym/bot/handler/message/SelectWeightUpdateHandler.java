package ru.vichukano.gym.bot.handler.message;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Exercise;
import ru.vichukano.gym.bot.domain.dto.User;

import java.math.BigDecimal;
import java.util.Arrays;

import static ru.vichukano.gym.bot.domain.Command.CANCEL;
import static ru.vichukano.gym.bot.domain.Command.EXERCISE;
import static ru.vichukano.gym.bot.domain.State.SELECT_EXERCISE;
import static ru.vichukano.gym.bot.domain.State.SELECT_REPS;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class SelectWeightUpdateHandler extends AbstractUpdateHandler {

    @Override
    public SendMessage handle(Update message) {
        var out = super.handle(message);
        String text = text(message);
        if (Arrays.stream(Exercise.values()).map(Exercise::getCommand).anyMatch(c -> c.equals(text))) {
            USER_STORE.USERS.asMap().get(userId(message)).setState(SELECT_EXERCISE);
            return new SelectExerciseHandler().handle(message);
        }
        if (CANCEL.getCommand().equals(text) || EXERCISE.getCommand().equals(text)) {
            log().debug("Undo exercise for user: {}", userName(message));
            USER_STORE.USERS.asMap().get(userId(message)).setState(SELECT_EXERCISE);
            return new SelectExerciseHandler().handle(message);
        }
        try {
            var weight = new BigDecimal(text);
            if (weight.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Must be positive digit");
            log.debug("Select weight in KG: {}", weight);
            out.setText("You select " + text + "KG. Select reps for this weight or " + CANCEL.getCommand() + " for undo");
            User user = USER_STORE.USERS.asMap().get(userId(message));
            user.getTraining()
                    .getExercises()
                    .getLast()
                    .getWeights()
                    .add(weight);
            user.setState(SELECT_REPS);
        } catch (Exception e) {
            out.setText("Invalid weight " + text + "! Weight must be digit");
        }
        return out;
    }

    @Override
    protected Logger log() {
        return log;
    }
}
