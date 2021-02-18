package ru.vichukano.gym.bot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;

import static ru.vichukano.gym.bot.domain.Command.STOP;
import static ru.vichukano.gym.bot.domain.State.START_TRAINING;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
@AllArgsConstructor
public class CompoundMessageHandler implements MessageHandler {
    private final Map<State, MessageHandler> stateToHandler;

    @Override
    public SendMessage handle(Update message) {
        log.trace("Got message: {}", message);
        if (!checkMessage(message)) {
            //TODO: Может лучше кидать исключения???
            return null;
        }
        User user = USER_STORE.USERS.asMap()
                .computeIfAbsent(userId(message), id ->
                        new User(id, userName(message), new Training(LocalDateTime.now(), new LinkedList<>()), START_TRAINING));
        State state = user.getState();
        log.debug("Start to handle user {}", user);
        if (STOP.getCommand().equals(text(message))) {
            return stateToHandler.get(State.STOP).handle(message);
        }
        return stateToHandler.get(state).handle(message);
    }

    private boolean checkMessage(Update message) {
        return message.hasMessage() && message.getMessage().hasText();
    }
}
