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
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Exercise;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;
import ru.vichukano.gym.bot.util.MessageUtils;

import static ru.vichukano.gym.bot.actors.ExerciseActor.ExerciseCommand;
import static ru.vichukano.gym.bot.domain.Command.CANCEL;
import static ru.vichukano.gym.bot.domain.Exercise.*;

public class ExerciseActor extends AbstractBehavior<ExerciseCommand> {

    private ExerciseActor(ActorContext<ExerciseCommand> context) {
        super(context);
    }

    public static Behavior<ExerciseCommand> create() {
        return Behaviors.setup(ExerciseActor::new);
    }

    @Override
    public Receive<ExerciseCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(SelectExercise.class, this::onSelectExerciseReceive)
                .build();
    }

    private Behavior<ExerciseCommand> onSelectExerciseReceive(SelectExercise exercise) {
        getContext().getLog().debug("Receive message: {}", exercise);
        Update update = exercise.update;
        String chatID = MessageUtils.chatId(update);
        String text = MessageUtils.text(update) != null ? MessageUtils.text(update) : MessageUtils.queryData(update);
        var out = new SendMessage();
        out.setChatId(chatID);
        exercise.user.setState(State.SELECT_EXERCISE);
        if (BENCH_PRESS.getCommand().equals(text)) {
            out.setText("Start to bench. Input weight in KG or " + CANCEL.getCommand() + " for undo");
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(BENCH_PRESS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (SQUAT.getCommand().equals(text)) {
            out.setText("Start to squat. Input weight in KG or " + CANCEL.getCommand() + " for undo");
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(SQUAT.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (DEAD_LIFT.getCommand().equals(text)) {
            out.setText("Start to lift. Input weight in KG or " + CANCEL.getCommand() + " for undo");
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(DEAD_LIFT.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (OVERHEAD_PRESS.getCommand().equals(text)) {
            out.setText("Start to overhead press. Input weight in KG or " + CANCEL.getCommand() + " for undo");
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(OVERHEAD_PRESS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (DUMBBELLS_OVERHEAD_PRESS.getCommand().equals(text)) {
            out.setText("Start to overhead dumbbells press. Input weight in KG or " + CANCEL.getCommand() + " for undo");
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(DUMBBELLS_OVERHEAD_PRESS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (DUMBBELLS_BENCH_PRESS.getCommand().equals(text)) {
            out.setText("Start to bench dumbbells press. Input weight in KG or " + CANCEL.getCommand() + " for undo");
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(DUMBBELLS_BENCH_PRESS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (ABS.getCommand().equals(text)) {
            out.setText("Start to abs. Input weight in KG. If you do with body weigh, than input 0 or " + CANCEL.getCommand() + " for undo");
            out.setReplyMarkup(KeyboardFactory.zeroWeightButton());
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(ABS.name()));
            user.setState(State.SELECT_WEIGHT);
        } else if (PULL_UP.getCommand().equals(text)) {
            out.setText("Start to pull ups. Input weight in KG. If you do it with body weight, than input 0 or " + CANCEL.getCommand() + " for undo");
            out.setReplyMarkup(KeyboardFactory.zeroWeightButton());
            User user = exercise.user;
            user.getTraining().getExercises().add(new Exercise(PULL_UP.name()));
            user.setState(State.SELECT_WEIGHT);
        } else {
            out.setText("Input correct exercise form:\n");
            out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());
        }
        ActorRef<BotActor.BotCommand> replyTo = exercise.replyTo;
        replyTo.tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface ExerciseCommand {
    }

    @Value
    public static class SelectExercise implements ExerciseCommand {
        Update update;
        User user;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
