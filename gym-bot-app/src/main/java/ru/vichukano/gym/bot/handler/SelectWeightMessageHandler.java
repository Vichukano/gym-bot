package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Exercise;
import ru.vichukano.gym.bot.domain.dto.User;

import java.math.BigDecimal;
import java.util.Arrays;

import static ru.vichukano.gym.bot.domain.State.SELECT_EXERCISE;
import static ru.vichukano.gym.bot.domain.State.SELECT_REPS;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class SelectWeightMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        String text = text(message);
        if (Arrays.stream(Exercise.values()).map(Exercise::getCommand).anyMatch(c -> c.equals(text))) {
            USER_STORE.STATES.put(userId(message), SELECT_EXERCISE);
            return new SelectExerciseHandler().handle(message);
        }
        try {
            var weight = new BigDecimal(text);
            log.debug("Select weight: {}", weight);
            out.setText("You select " + text + "KG. Select reps for this weight");
            USER_STORE.STATES.put(userId(message), SELECT_REPS);
            USER_STORE.USERS.get(userId(message))
                    .getTraining()
                    .getExercises()
                    .getLast()
                    .setWeight(weight);
        } catch (Exception e) {
            out.setText("Invalid weight " + text + "! Weight must be digit");
        }
        return out;
    }

}
