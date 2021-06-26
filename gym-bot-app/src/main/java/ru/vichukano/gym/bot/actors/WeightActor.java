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
import ru.vichukano.gym.bot.util.MessageUtils;

import java.math.BigDecimal;

import static ru.vichukano.gym.bot.domain.Command.CANCEL;
import static ru.vichukano.gym.bot.domain.State.SELECT_REPS;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.userId;

public class WeightActor extends AbstractBehavior<WeightActor.WeightCommand> {

    private WeightActor(ActorContext<WeightCommand> context) {
        super(context);
    }

    public static Behavior<WeightCommand> create() {
        return Behaviors.setup(WeightActor::new);
    }

    @Override
    public Receive<WeightCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(SelectWeight.class, this::onSelectWeightReceive)
                .build();
    }

    private Behavior<WeightCommand> onSelectWeightReceive(SelectWeight weightCommand) {
        getContext().getLog().debug("Receive message: {}", weightCommand);
        Update update = weightCommand.update;
        String text = MessageUtils.text(update);
        var out = new SendMessage();
        out.setChatId(MessageUtils.chatId(update));
        try {
            var weight = new BigDecimal(text);
            if (weight.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Must be positive digit");
            }
            out.setText("You select " + text + "KG. Select reps for this weight or " + CANCEL.getCommand() + " for undo");
            User user = USER_STORE.USERS.asMap().get(userId(update));
            user.getTraining()
                    .getExercises()
                    .getLast()
                    .getWeights()
                    .add(weight);
            user.setState(SELECT_REPS);
        } catch (Exception e) {
            getContext().getLog().error("Exception while processing select weight message: ", e);
            out.setText("Invalid weight " + text + "! Weight must be digit");
        }
        weightCommand.replyTo.tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface WeightCommand {

    }

    @Value
    public static class SelectWeight implements WeightCommand {
        Update update;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
