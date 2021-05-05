package ru.vichukano.gym.bot.handler.message;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.factory.KeyboardFactory;

import static ru.vichukano.gym.bot.domain.Command.HELP;
import static ru.vichukano.gym.bot.domain.Command.START;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.text;
import static ru.vichukano.gym.bot.util.MessageUtils.userId;

@Slf4j
public class StartUpdateHandler extends AbstractUpdateHandler {

    @Override
    public SendMessage handle(Update message) {
        var out = super.handle(message);
        if (START.getCommand().equals(text(message))) {
            out.setText("Choose exercise from:\n");
            out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());
            USER_STORE.USERS.asMap().get(userId(message)).setState(State.SELECT_EXERCISE);
        } else if (HELP.getCommand().equals(text(message))) {
            out.setText("Hi! I am a gym training bot, I can help to track your progress in the gym."
                    + " Type " + START.getCommand() + " for start your training session."
                    + " You can choose exercises from list, set reps and weight,"
                    + " and after training session I show your training report.");
        } else {
            out.setText("Send me "
                    + START.getCommand()
                    + " for start training"
                    + " or "
                    + HELP.getCommand()
                    + " for help.");
            out.setReplyMarkup(KeyboardFactory.startKeyboard());
        }
        return out;
    }

    @Override
    protected Logger log() {
        return log;
    }

}
