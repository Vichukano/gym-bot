package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Exercise;
import ru.vichukano.gym.bot.domain.State;

import static ru.vichukano.gym.bot.domain.Command.START;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
public class StartMessageHandler implements MessageHandler {

    @Override
    public SendMessage handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        if (START.getCommand().equals(text(message))) {
            out.setText("Hi! Choose exercise from: " + Exercise.printAll());
            USER_STORE.USERS.asMap().get(userId(message)).setState(State.SELECT_EXERCISE);
        } else {
            out.setText("Send me "
                    + START.getCommand()
                    + " for start training");
        }
        return out;
    }

}
