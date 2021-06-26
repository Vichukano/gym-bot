package ru.vichukano.gym.bot.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.factory.KeyboardFactory;

import static ru.vichukano.gym.bot.domain.Command.HELP;
import static ru.vichukano.gym.bot.domain.Command.START;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

public class StartActor extends AbstractBehavior<StartActor.StartCommand> {

    private StartActor(ActorContext<StartCommand> context) {
        super(context);
    }

    public static Behavior<StartCommand> create() {
        return Behaviors.setup(StartActor::new);
    }

    @Override
    public Receive<StartCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartTraining.class, this::onStartTrainingReceive)
                .build();
    }

    private Behavior<StartCommand> onStartTrainingReceive(StartTraining start) {
        getContext().getLog().debug("Receive message: {}", start);
        val out = new SendMessage();
        Update update = start.update;
        out.setChatId(chatId(update));
        if (START.getCommand().equals(text(update))) {
            out.setText("Choose exercise from:\n");
            out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());
            USER_STORE.USERS.asMap().get(userId(update)).setState(State.SELECT_EXERCISE);
        } else {
            out.setText("Send me "
                    + START.getCommand()
                    + " for start training"
                    + " or "
                    + HELP.getCommand()
                    + " for help.");
            out.setReplyMarkup(KeyboardFactory.startKeyboard());
        }
        start.getReplyTo().tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface StartCommand {
    }

    @Value
    public static class StartTraining implements StartCommand {
        Update update;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
