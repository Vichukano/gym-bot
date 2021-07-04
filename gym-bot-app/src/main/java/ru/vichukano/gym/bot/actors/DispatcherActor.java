package ru.vichukano.gym.bot.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;
import ru.vichukano.gym.bot.service.UserService;
import ru.vichukano.gym.bot.util.MessageUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static ru.vichukano.gym.bot.domain.Command.*;
import static ru.vichukano.gym.bot.domain.State.START_TRAINING;

public class DispatcherActor extends AbstractBehavior<DispatcherActor.DispatcherCommand> {
    private static final String USER_STATE_BOT_PREFIX = "user-state-actor-";
    public final Map<String, ActorRef<UserStateActor.StateCommand>> userActors;
    private final ActorRef<BotActor.BotCommand> mainActor;
    private final ActorRef<HelpActor.HelpCommand> helpActor;
    private final ActorRef<StartActor.StartCommand> startActor;
    private final ActorRef<ExerciseActor.ExerciseCommand> exerciseActor;
    private final ActorRef<WeightActor.WeightCommand> weightActor;
    private final ActorRef<RepsActor.RepsCommand> repsActor;
    private final ActorRef<CancelActor.CancelCommand> cancelActor;
    private final ActorRef<StopActor.StopCommand> stopActor;
    private final ActorRef<ReportActor.ReportCommand> reportActor;

    private DispatcherActor(ActorContext<DispatcherCommand> context, ActorRef<BotActor.BotCommand> mainActor, UserService userService) {
        super(context);
        this.mainActor = mainActor;
        this.userActors = new ConcurrentHashMap<>();
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

    public static Behavior<DispatcherCommand> create(ActorRef<BotActor.BotCommand> mainActor, UserService userService) {
        return Behaviors.<DispatcherCommand>supervise(Behaviors.setup(ctx -> new DispatcherActor(ctx, mainActor, userService)))
                .onFailure(SupervisorStrategy.restart().withStopChildren(false));
    }

    @Override
    public Receive<DispatcherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(DispatcherMessage.class, this::onBotCommandReceive)
                .onMessage(UserStateAnswer.class, this::onUserStateAnswerReceive)
                .onMessage(RemoveUserState.class, this::onRemoveUserReceive)
                .build();
    }

    private Behavior<DispatcherCommand> onBotCommandReceive(DispatcherMessage message) {
        getContext().getLog().debug("Receive command: {}", message);
        Update update = message.update;
        String userId = MessageUtils.userId(update);
        String name = MessageUtils.userName(update);
        ActorRef<UserStateActor.StateCommand> userRef = userActors.get(userId);
        if (Objects.isNull(userRef)) {
            userRef = getContext().spawn(UserStateActor.create(getContext().getSelf(), userId, name, START_TRAINING), USER_STATE_BOT_PREFIX + userId);
            userActors.put(userId, userRef);
            getContext().getLog().info("Create new user context: {}", userRef);
        }
        userRef.tell(new UserStateActor.GetState(LocalDateTime.now(), update));
        return this;
    }

    private Behavior<DispatcherCommand> onUserStateAnswerReceive(UserStateAnswer message) {
        try {
            getContext().getLog().debug("Receive command: {}", message);
            Update update = message.getUpdate();
            String text = getText(update);
            getContext().getLog().debug("Text of message: {}", text);
            User user = message.user;
            if (HELP.getCommand().equals(text)) {
                helpActor.tell(new HelpActor.HelpCommand(update, mainActor));
            } else if (START.getCommand().equals(text)) {
                startActor.tell(new StartActor.StartTraining(update, user, mainActor));
            } else if (CANCEL.getCommand().equals(text)) {
                cancelActor.tell(new CancelActor.CancelExercise(update, user, mainActor));
            } else if (STOP.getCommand().equals(text)) {
                stopActor.tell(new StopActor.StopTraining(update, user, userActors.get(user.getId()), mainActor));
            } else if (EXERCISE.getCommand().equals(text)) {
                exerciseActor.tell(new ExerciseActor.SelectExercise(update, user, mainActor));
            } else if (State.SELECT_EXERCISE == user.getState()) {
                exerciseActor.tell(new ExerciseActor.SelectExercise(update, user, mainActor));
            } else if (State.SELECT_WEIGHT == user.getState()) {
                weightActor.tell(new WeightActor.SelectWeight(update, user, mainActor));
            } else if (State.SELECT_REPS == user.getState()) {
                repsActor.tell(new RepsActor.SelectReps(update, user, mainActor));
            } else if (REPORT.getCommand().equals(text)) {
                reportActor.tell(new ReportActor.TrainingReport(update, user, mainActor));
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
                mainActor.tell(new BotActor.ReplyMessage(out));
                getContext().getLog().warn("Default case, nothing to dispatch");
            }
        } catch (Exception e) {
            getContext().getLog().error("Exception while dispatching message: {}", message, e);
        }
        return this;
    }

    private Behavior<DispatcherCommand> onRemoveUserReceive(RemoveUserState remove) {
        getContext().getLog().debug("Receive remove user message: {}", remove);
        userActors.remove(remove.getId());
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

    public interface DispatcherCommand {
    }

    @Value
    public static class DispatcherMessage implements DispatcherCommand {
        Update update;
    }

    @Value
    public static class UserStateAnswer implements DispatcherCommand {
        Update update;
        User user;
    }

    @Value
    public static class RemoveUserState implements DispatcherCommand {
        String id;
    }
}
