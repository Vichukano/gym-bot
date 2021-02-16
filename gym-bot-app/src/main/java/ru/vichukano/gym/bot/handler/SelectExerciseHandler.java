package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;

import static ru.vichukano.gym.bot.store.UserStateStore.STATE_STORE;
import static ru.vichukano.gym.bot.util.MsgUtils.*;

@Slf4j
public class SelectExerciseHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        String text = text(message);
        if ("/bench".equals(text)) {
            out.setText("Start to bench. Input weight");
            STATE_STORE.STORE.put(userId(message), State.SELECT_WEIGHT);
        } else if ("/squat".equals(text)) {
            out.setText("Start to squat. Input weight");
            STATE_STORE.STORE.put(userId(message), State.SELECT_WEIGHT);
        } else if ("/lift".equals(text)) {
            out.setText("Start to lift. Input weight");
            STATE_STORE.STORE.put(userId(message), State.SELECT_WEIGHT);
        } else {
            out.setText("Input correct exercise form: /bench, /squat, /lift or type /stop for exit");
        }
        return out;
    }

}
