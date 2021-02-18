package ru.vichukano.gym.bot.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.domain.dto.User;

import static ru.vichukano.gym.bot.domain.Exercise.printAll;
import static ru.vichukano.gym.bot.domain.State.SELECT_WEIGHT;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

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
            out.setText("You select "
                    + text
                    + "reps. Select weight or "
                    + Command.STOP.getCommand()
                    + " for finish training or choose another exercise from: "
                    + printAll());
            User user = USER_STORE.USERS.asMap().get(userId(message));
            user.getTraining()
                    .getExercises()
                    .getLast()
                    .getReps()
                    .add(reps);
            user.setState(SELECT_WEIGHT);
        } catch (Exception e) {
            out.setText("Invalid reps " + text + "! Reps must be digit");
        }
        return out;
    }

}
