package ru.vichukano.gym.bot.actors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
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
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.service.UserService;
import ru.vichukano.gym.bot.util.MessageUtils;

class StopActor extends AbstractBehavior<StopActor.StopCommand> {
    private static final String STOP_TEXT = "Stop training. Your results:\n"
            + "Training session time: "
            + "%s"
            + " minutes"
            + "\nExercises:\n"
            + "%s"
            + "\ntype "
            + "%s"
            + " for send training report.";
    private final UserService service;

    private StopActor(ActorContext<StopCommand> context, UserService service) {
        super(context);
        this.service = service;
    }

    public static Behavior<StopCommand> create(UserService service) {
        return Behaviors.<StopCommand>supervise(Behaviors.setup(ctx -> new StopActor(ctx, service)))
                .onFailure(SupervisorStrategy.restart());
    }

    @Override
    public Receive<StopCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(StopTraining.class, this::onStopTrainingReceive)
                .build();
    }

    private Behavior<StopCommand> onStopTrainingReceive(StopTraining stop) {
        getContext().getLog().debug("Receive message: {}", stop);
        Update update = stop.update;
        var out = new SendMessage();
        out.setChatId(MessageUtils.chatId(update));
        User user = stop.user;
        Training training = user.getTraining();
        out.setText(String.format(STOP_TEXT,
                Duration.between(training.getTime(), LocalDateTime.now()).toMinutes(),
                training.getExercises().stream().map(Objects::toString).collect(Collectors.joining("\n")),
                Command.REPORT.getCommand()));
        service.saveUserTrainingInfo(user);
        stop.userState.tell(new UserStateActor.DestroyMessage());
        stop.replyTo.tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface StopCommand {
    }

    @Value
    public static class StopTraining implements StopCommand {
        Update update;
        User user;
        ActorRef<UserStateActor.StateCommand> userState;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
