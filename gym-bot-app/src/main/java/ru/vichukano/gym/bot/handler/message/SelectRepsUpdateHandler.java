package ru.vichukano.gym.bot.handler.message;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.dto.User;

import static ru.vichukano.gym.bot.domain.Command.*;
import static ru.vichukano.gym.bot.domain.State.SELECT_EXERCISE;
import static ru.vichukano.gym.bot.domain.State.SELECT_WEIGHT;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class SelectRepsUpdateHandler extends AbstractUpdateHandler {

    @Override
    public SendMessage handle(Update message) {
        var out = super.handle(message);
        String text = text(message);
        if (CANCEL.getCommand().equals(text)) {
            log().debug("Undo exercise for user: {}", userName(message));
            User user = USER_STORE.USERS.asMap().get(userId(message));
            user.getTraining()
                    .getExercises()
                    .getLast()
                    .getWeights()
                    .removeLast();
            USER_STORE.USERS.asMap().get(userId(message)).setState(SELECT_EXERCISE);
            return new SelectExerciseHandler().handle(message);
        }
        try {
            var reps = Integer.valueOf(text);
            if (reps <= 0) throw new IllegalArgumentException("Must be positive digit");
            log.debug("Select reps: {}", reps);
            out.setText("You select "
                    + text
                    + "reps.\nSelect weight or "
                    + STOP.getCommand()
                    + " for finish training or "
                    + EXERCISE.getCommand()
                    + " for choose another exercise.");
            User user = USER_STORE.USERS.asMap().get(userId(message));
            user.getTraining()
                    .getExercises()
                    .getLast()
                    .getReps()
                    .add(reps);
            user.setState(SELECT_WEIGHT);
        } catch (Exception e) {
            out.setText("Invalid reps " + text + "! Reps must be digit");
        }
        return out;
    }

    @Override
    protected Logger log() {
        return log;
    }

}
