package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.domain.Exercise;

import java.util.Arrays;
import java.util.stream.Collectors;

import static ru.vichukano.gym.bot.domain.State.SELECT_EXERCISE;
import static ru.vichukano.gym.bot.domain.State.SELECT_WEIGHT;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class SelectRepsMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        String text = text(message);
        try {
            var reps = Integer.valueOf(text);
            log.debug("Select reps: {}", reps);
            out.setText("You select "
                    + text
                    + "reps. Select weight or "
                    + Command.STOP.getCommand()
                    + " for finish training"
                    + " or choose another exercise from: "
                    //TODO: Вынести в метод енама
                    + Arrays.stream(Exercise.values()).map(Exercise::getCommand).collect(Collectors.joining(",")));
            USER_STORE.STATES.put(userId(message), SELECT_WEIGHT);
            USER_STORE.USERS.get(userId(message))
                    .getTraining()
                    .getExercises()
                    .getLast()
                    .setReps(reps);
        } catch (Exception e) {
            out.setText("Invalid reps " + text + "! Reps must be digit");
        }
        return out;
    }

}
