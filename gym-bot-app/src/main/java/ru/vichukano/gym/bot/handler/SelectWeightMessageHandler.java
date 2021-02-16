package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

import static ru.vichukano.gym.bot.domain.State.SELECT_REPS;
import static ru.vichukano.gym.bot.store.UserStateStore.STATE_STORE;
import static ru.vichukano.gym.bot.util.MsgUtils.*;

@Slf4j
public class SelectWeightMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        String text = text(message);
        try {
            var weight = new BigDecimal(text);
            log.debug("Select weight: {}", weight);
            out.setText("You select " + text + "KG. Select reps for this weight");
            STATE_STORE.STORE.put(userId(message), SELECT_REPS);
        } catch (Exception e) {
            out.setText("Invalid weight " + text + "! Weight must be digit");
        }
        return out;
    }

}
