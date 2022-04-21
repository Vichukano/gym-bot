package ru.vichukano.gym.bot.actors;

import static ru.vichukano.gym.bot.domain.Command.EXERCISE;
import static ru.vichukano.gym.bot.domain.Command.STOP;
import static ru.vichukano.gym.bot.domain.State.SELECT_WEIGHT;
import java.util.Objects;
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
import ru.vichukano.gym.bot.domain.Exercise;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;
import ru.vichukano.gym.bot.util.MessageUtils;

class RepsActor extends AbstractBehavior<RepsActor.RepsCommand> {
    private static final String REPS_TEXT = "You select %s reps.\n"
            + "Select weight or %s for finish training or %s for choose another exercise.";

    private RepsActor(ActorContext<RepsCommand> context) {
        super(context);
    }

    public static Behavior<RepsCommand> create() {
        return Behaviors.supervise(Behaviors.setup(RepsActor::new))
                .onFailure(SupervisorStrategy.restart());
    }

    @Override
    public Receive<RepsCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(SelectReps.class, this::onSelectRepsReceive)
                .build();
    }

    private Behavior<RepsCommand> onSelectRepsReceive(SelectReps repsCommand) {
        getContext().getLog().debug("Receive message: {}", repsCommand);
        Update update = repsCommand.update;
        String text = MessageUtils.text(update);
        var out = new SendMessage();
        out.setChatId(MessageUtils.chatId(update));
        try {
            var reps = Integer.parseInt(text);
            if (reps <= 0) {
                throw new IllegalArgumentException("Must be positive digit");
            }
            out.setText(String.format(REPS_TEXT, text, STOP.getCommand(), EXERCISE.getCommand()));
            User user = repsCommand.user;
            user.getTraining()
                    .getExercises()
                    .getLast()
                    .getReps()
                    .add(reps);
            user.setState(SELECT_WEIGHT);
            var keyboard = KeyboardFactory.afterSetKeyboard();
            String exercise = user.getTraining().getExercises().getLast().getName();
            if (Objects.equals(exercise, Exercise.ABS.name())
                    || Objects.equals(exercise, Exercise.PULL_UP.name())
                    || Objects.equals(exercise, Exercise.PUSH_UP.name())
                    || Objects.equals(exercise, Exercise.PUSH_UP_ON_BARS.name())) {
                keyboard.getKeyboard().add(KeyboardFactory.zeroWeightButton().getKeyboard().get(0));
            }
            out.setReplyMarkup(keyboard);
        } catch (Exception e) {
            getContext().getLog().error("Exception while processing select reps command:", e);
            out.setText("Invalid reps " + text + "! Reps must be digit");
        }
        repsCommand.replyTo.tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface RepsCommand {
    }

    @Value
    public static class SelectReps implements RepsCommand {
        Update update;
        User user;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
