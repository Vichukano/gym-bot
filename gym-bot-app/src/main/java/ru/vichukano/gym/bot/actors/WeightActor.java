package ru.vichukano.gym.bot.actors;

import static ru.vichukano.gym.bot.domain.Command.CANCEL;
import static ru.vichukano.gym.bot.domain.State.SELECT_REPS;
import java.math.BigDecimal;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.util.MessageUtils;

class WeightActor extends AbstractBehavior<WeightActor.WeightCommand> {
    private static final String WEIGHT_TEXT = "You select %sKG. Select reps for this weight or %s for undo";
    private static final String INVALID_WEIGHT_TEXT = "Invalid weight %s! Weight must be digit";

    private WeightActor(ActorContext<WeightCommand> context) {
        super(context);
    }

    public static Behavior<WeightCommand> create() {
        return Behaviors.supervise(Behaviors.setup(WeightActor::new))
                .onFailure(SupervisorStrategy.restart());
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
            out.setText(String.format(WEIGHT_TEXT, text, CANCEL.getCommand()));
            User user = weightCommand.user;
            user.getTraining()
                    .getExercises()
                    .getLast()
                    .getWeights()
                    .add(weight);
            user.setState(SELECT_REPS);
        } catch (Exception e) {
            getContext().getLog().error("Exception while processing select weight message: ", e);
            out.setText(String.format(INVALID_WEIGHT_TEXT, text));
        }
        weightCommand.replyTo.tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface WeightCommand {
    }

    @Value
    public static class SelectWeight implements WeightCommand {
        Update update;
        User user;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
