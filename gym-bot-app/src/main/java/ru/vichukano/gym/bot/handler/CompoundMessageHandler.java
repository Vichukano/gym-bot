package ru.vichukano.gym.bot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.util.MsgUtils;

import java.util.Map;

import static ru.vichukano.gym.bot.domain.State.START_TRAINING;
import static ru.vichukano.gym.bot.store.UserStateStore.STATE_STORE;
import static ru.vichukano.gym.bot.util.MsgUtils.*;
import static ru.vichukano.gym.bot.util.MsgUtils.userId;

@Slf4j
@AllArgsConstructor
public class CompoundMessageHandler implements MessageHandler {
    private final Map<State, MessageHandler> stateToHandler;

    @Override
    public SendMessage handle(Update message) {
        log.trace("Got message: {}", message);
        if (!checkMsg(message)) {
            //Todo: Может лучше кидать исключения
            return null;
        }
        String userId = userId(message);
        State state = STATE_STORE.STORE.getOrDefault(userId, START_TRAINING);
        log.debug("State: {} for userId: {}", state, userId);
        if ("/stop".equals(text(message))) {
            return stateToHandler.get(State.STOP).handle(message);
        }
        return stateToHandler.get(state).handle(message);
    }

    private boolean checkMsg(Update message) {
        return message.hasMessage() && message.getMessage().hasText();
    }

}