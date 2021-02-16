package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;

import static ru.vichukano.gym.bot.store.UserStateStore.STATE_STORE;
import static ru.vichukano.gym.bot.util.MsgUtils.*;
import static ru.vichukano.gym.bot.util.MsgUtils.userId;

@Slf4j
public class StopMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        String text = text(message);
        if ("/stop".equals(text)) {
            out.setText("Stop training. Your results: .....");
        } else {
            out.setText("Something goes wrong...");
        }
        STATE_STORE.STORE.put(userId(message), State.START_TRAINING);
        return out;
    }

}
