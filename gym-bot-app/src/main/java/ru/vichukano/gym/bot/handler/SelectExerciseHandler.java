package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Exercise;
import ru.vichukano.gym.bot.domain.dto.User;

import static ru.vichukano.gym.bot.domain.Exercise.*;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class SelectExerciseHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        String text = text(message);
        if (BENCH_PRESS.getCommand().equals(text)) {
            out.setText("Start to bench. Input weight");
            User user = USER_STORE.USERS.asMap().get(userId(message));
            user.getTraining().getExercises().add(new Exercise(BENCH_PRESS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (SQUAT.getCommand().equals(text)) {
            out.setText("Start to squat. Input weight");
            User user = USER_STORE.USERS.asMap().get(userId(message));
            user.getTraining().getExercises().add(new Exercise(BENCH_PRESS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (DEAD_LIFT.getCommand().equals(text)) {
            out.setText("Start to lift. Input weight");
            User user = USER_STORE.USERS.asMap().get(userId(message));
            user.getTraining().getExercises().add(new Exercise(BENCH_PRESS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else {
            out.setText("Input correct exercise form:"
                    + printAll()
                    + " or type "
                    + Command.STOP.getCommand()
                    + " for exit");
        }
        return out;
    }

}
