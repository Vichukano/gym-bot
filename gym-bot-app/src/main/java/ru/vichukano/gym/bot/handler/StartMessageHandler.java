package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

import static ru.vichukano.gym.bot.domain.Command.START;
import static ru.vichukano.gym.bot.domain.Exercise.*;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class StartMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        User user = USER_STORE.USERS.computeIfAbsent(userId(message), id -> new User(id, userName(message), new Training(LocalDateTime.now(), new LinkedList<>())));
        log.debug("Handle user: {}", user);
        if (START.getCommand().equals(text(message))) {
            out.setText("Hi! Choose exercise from: "
                    + BENCH_PRESS.getCommand()
                    + ", "
                    + SQUAT.getCommand()
                    + ", "
                    + DEAD_LIFT.getCommand());
            USER_STORE.STATES.put(userId(message), State.SELECT_EXERCISE);
        } else {
            out.setText("Send me "
                    + START.getCommand()
                    + " for start training");
        }
        return out;
    }

}
