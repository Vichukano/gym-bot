package ru.vichukano.gym.bot.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;
import ru.vichukano.gym.bot.util.MessageUtils;

import static ru.vichukano.gym.bot.domain.State.SELECT_EXERCISE;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.userId;

public class CancelActor extends AbstractBehavior<CancelActor.CancelCommand> {

    private CancelActor(ActorContext<CancelCommand> context) {
        super(context);
    }

    public static Behavior<CancelCommand> create() {
        return Behaviors.setup(CancelActor::new);
    }

    @Override
    public Receive<CancelCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(CancelExercise.class, this::onCancelExerciseReceive)
                .build();
    }

    private Behavior<CancelCommand> onCancelExerciseReceive(CancelExercise cancelCommand) {
        getContext().getLog().debug("Receive message: {}", cancelCommand);
        Update update = cancelCommand.update;
        var out = new SendMessage();
        out.setChatId(MessageUtils.chatId(update));
        User user = USER_STORE.USERS.asMap().get(userId(update));
        user.getTraining()
                .getExercises()
                .getLast()
                .getWeights()
                .removeLast();
        USER_STORE.USERS.asMap().get(userId(update)).setState(SELECT_EXERCISE);
        out.setText("Successfully undo exercise, input new exercise form:\n");
        out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());
        cancelCommand.replyTo.tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface CancelCommand {
    }

    @Value
    public static class CancelExercise implements CancelCommand {
        Update update;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
