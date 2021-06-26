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
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;
import ru.vichukano.gym.bot.service.UserService;
import ru.vichukano.gym.bot.util.MessageUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;

import static ru.vichukano.gym.bot.domain.Command.*;
import static ru.vichukano.gym.bot.domain.State.START_TRAINING;
import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.userId;
import static ru.vichukano.gym.bot.util.MessageUtils.userName;

public class DispatcherActor extends AbstractBehavior<DispatcherActor.DispatcherCommand> {
    private final ActorRef<HelpActor.HelpCommand> helpActor;
    private final ActorRef<StartActor.StartCommand> startActor;
    private final ActorRef<ExerciseActor.ExerciseCommand> exerciseActor;
    private final ActorRef<WeightActor.WeightCommand> weightActor;
    private final ActorRef<RepsActor.RepsCommand> repsActor;
    private final ActorRef<CancelActor.CancelCommand> cancelActor;
    private final ActorRef<StopActor.StopCommand> stopActor;
    private final ActorRef<ReportActor.ReportCommand> reportActor;

    private DispatcherActor(ActorContext<DispatcherCommand> context, UserService userService) {
        super(context);
        getContext().getLog().info("Start to init actors");
        this.helpActor = getContext().spawn(HelpActor.create(), "help-actor");
        this.startActor = getContext().spawn(StartActor.create(), "start-actor");
        this.exerciseActor = getContext().spawn(ExerciseActor.create(), "exercise-actor");
        this.weightActor = getContext().spawn(WeightActor.create(), "weight-actor");
        this.repsActor = getContext().spawn(RepsActor.create(), "reps-actor");
        this.cancelActor = getContext().spawn(CancelActor.create(), "cancel-actor");
        this.stopActor = getContext().spawn(StopActor.create(userService), "stop-actor");
        this.reportActor = getContext().spawn(ReportActor.create(userService), "report-actor");
        getContext().getLog().info("Create actor system: {}", getContext().getSystem().printTree());
    }

    public static Behavior<DispatcherCommand> create(UserService userService) {
        return Behaviors.setup(ctx -> new DispatcherActor(ctx, userService));
    }

    private Behavior<DispatcherCommand> onCommandReceive(DispatcherMessage message) {
        try {
            getContext().getLog().debug("Receive command: {}", message);
            Update update = message.getUpdate();
            String text = getText(update);
            getContext().getLog().debug("Text of message: {}", text);
            ActorRef<BotActor.BotCommand> replyTo = message.getReplyTo();
            User user = USER_STORE.USERS.asMap().computeIfAbsent(userId(update), id ->
                    new User(id, userName(update), new Training(LocalDateTime.now(), new LinkedList<>()), START_TRAINING));
            if (HELP.getCommand().equals(text)) {
                helpActor.tell(new HelpActor.HelpCommand(update, replyTo));
            } else if (START.getCommand().equals(text)) {
                startActor.tell(new StartActor.StartTraining(update, replyTo));
            } else if (CANCEL.getCommand().equals(text)) {
                cancelActor.tell(new CancelActor.CancelExercise(update, replyTo));
            } else if (STOP.getCommand().equals(text)) {
                stopActor.tell(new StopActor.StopTraining(update, replyTo));
            } else if (EXERCISE.getCommand().equals(text)) {
                exerciseActor.tell(new ExerciseActor.SelectExercise(update, replyTo));
            } else if (State.SELECT_EXERCISE == user.getState()) {
                exerciseActor.tell(new ExerciseActor.SelectExercise(update, replyTo));
            } else if (State.SELECT_WEIGHT == user.getState()) {
                weightActor.tell(new WeightActor.SelectWeight(update, replyTo));
            } else if (State.SELECT_REPS == user.getState()) {
                repsActor.tell(new RepsActor.SelectReps(update, replyTo));
            } else if (REPORT.getCommand().equals(text)) {
                reportActor.tell(new ReportActor.TrainingReport(update, replyTo));
            } else {
                var out = new SendMessage();
                out.setChatId(MessageUtils.chatId(update));
                out.setText("Send me "
                        + START.getCommand()
                        + " for start training"
                        + " or "
                        + HELP.getCommand()
                        + " for help.");
                out.setReplyMarkup(KeyboardFactory.startKeyboard());
                replyTo.tell(new BotActor.ReplyMessage(out));
            }
            getContext().getLog().warn("Default case, nothing to dispatch");
        } catch (Exception e) {
            getContext().getLog().error("Exception while dispatching message: {}", message, e);
        }
        return this;
    }

    private String getText(Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery()) {
            getContext().getLog().warn("Update has no message or callback query! Update: {}", update);
            throw new IllegalStateException("Update has no message or callback query!");
        }
        String text = MessageUtils.text(update);
        return text != null ? text : MessageUtils.queryData(update);
    }

    @Override
    public Receive<DispatcherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(DispatcherMessage.class, this::onCommandReceive)
                .build();
    }

    public interface DispatcherCommand {
    }

    @Value
    public static class DispatcherMessage implements DispatcherCommand {
        Update update;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
