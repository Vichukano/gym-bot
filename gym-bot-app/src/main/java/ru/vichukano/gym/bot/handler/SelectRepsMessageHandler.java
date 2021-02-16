package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static ru.vichukano.gym.bot.domain.State.SELECT_WEIGHT;
import static ru.vichukano.gym.bot.store.UserStateStore.STATE_STORE;
import static ru.vichukano.gym.bot.util.MsgUtils.*;

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
            out.setText("You select " + text + "reps. Select weight or /stop for finish training");
            STATE_STORE.STORE.put(userId(message), SELECT_WEIGHT);
        } catch (Exception e) {
            out.setText("Invalid reps " + text + "! Reps must be digit");
        }
        return out;
    }

}
