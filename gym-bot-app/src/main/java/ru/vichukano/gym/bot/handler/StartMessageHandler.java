package ru.vichukano.gym.bot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;

import static ru.vichukano.gym.bot.store.UserStateStore.STATE_STORE;
import static ru.vichukano.gym.bot.util.MsgUtils.*;

@Slf4j
public class StartMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        if ("/start".equals(text(message))) {
            out.setText("Hi! Choose exercise from: /bench, /squat, /lift");
            STATE_STORE.STORE.put(userId(message), State.SELECT_EXERCISE);
        } else {
            out.setText("Send me /start for start training");
        }
        return out;
    }

}
