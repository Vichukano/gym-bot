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
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.service.UserService;
import ru.vichukano.gym.bot.util.MessageUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

public class StopActor extends AbstractBehavior<StopActor.StopCommand> {
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
                .onMessage(AddTrainingDescription.class, this::onAddTrainingDescriptionReceive)
                .build();
    }

    private Behavior<StopCommand> onStopTrainingReceive(StopTraining stop) {
        getContext().getLog().debug("Receive message: {}", stop);
        Update update = stop.update;
        var out = new SendMessage();
        out.setChatId(MessageUtils.chatId(update));
        User user = stop.user;
        Training training = user.getTraining();
        var text = MessageUtils.text(update);
        if (Command.STOP.getCommand().equalsIgnoreCase(text)) {
            text = "No description";
        }
        out.setText("Stop training. Your results:\n"
                + "Training session time: "
                + Duration.between(training.getTime(), LocalDateTime.now()).toMinutes()
                + " minutes"
                + "\nExercises:\n"
                + training.getExercises().stream().map(Objects::toString).collect(Collectors.joining("\n"))
                + "\nTraining description:\n"
                + text
                + "\ntype "
                + Command.REPORT.getCommand()
                + " for send training report."
        );
        user.setTrainingDescription(text);
        service.saveUserTrainingInfo(user);
        stop.userState.tell(new UserStateActor.DestroyMessage());
        stop.replyTo.tell(new BotActor.ReplyMessage(out));
        return this;
    }

    private Behavior<StopCommand> onAddTrainingDescriptionReceive(AddTrainingDescription description) {
        getContext().getLog().debug("Receive message: {}", description);
        Update update = description.update;
        var out = new SendMessage();
        out.setChatId(MessageUtils.chatId(update));
        User user = description.user;
        user.setState(State.STOP);
        out.setText("Write training description or type: " + Command.STOP.getCommand() + " for skip");
        description.replyTo.tell(new BotActor.ReplyMessage(out));
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
    
    @Value
    public static class AddTrainingDescription implements StopCommand {
      Update update;
      User user;
      ActorRef<UserStateActor.StateCommand> userState;
      ActorRef<BotActor.BotCommand> replyTo;
    }
}
