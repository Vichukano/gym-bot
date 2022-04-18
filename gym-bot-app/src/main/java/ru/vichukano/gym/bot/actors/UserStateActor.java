package ru.vichukano.gym.bot.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

public class UserStateActor extends AbstractBehavior<UserStateActor.StateCommand> {
    private static final int MAX_IDLE_TIME_MINUTES = 60;
    private static final Object TIMER_KEY = new Object();
    private final ActorRef<DispatcherActor.DispatcherCommand> dispatcher;
    private final User user;
    private LocalDateTime lastAccessTime;

    private UserStateActor(ActorContext<StateCommand> context,
                           ActorRef<DispatcherActor.DispatcherCommand> dispatcher,
                           String id,
                           String name,
                           State state) {
        super(context);
        this.dispatcher = dispatcher;
        this.user = new User(id, name, new Training(LocalDateTime.now(), new LinkedList<>()), state);
        this.lastAccessTime = LocalDateTime.now();
    }

    public static Behavior<StateCommand> create(ActorRef<DispatcherActor.DispatcherCommand> dispatcher,
                                                String id,
                                                String name,
                                                State state) {
        return Behaviors.<StateCommand>supervise(Behaviors.setup(ctx -> new UserStateActor(ctx, dispatcher, id, name, state)))
                .onFailure(SupervisorStrategy.restart());
    }

    @Override
    public Receive<StateCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(DestroyMessage.class, param -> Behaviors.stopped())
                .onMessage(GetState.class, this::onGetStateReceive)
                .onMessage(TryExpired.class, this::onTryExpiredReceive)
                .onSignal(PostStop.class, param -> {
                    getContext().getLog().info("User destroyed: {}, actor: {}", user, getContext().getSelf());
                    dispatcher.tell(new DispatcherActor.RemoveUserState(user.getId()));
                    return this;
                })
                .build();
    }

    private Behavior<StateCommand> onGetStateReceive(GetState message) {
        getContext().getLog().debug("Receive GetState message: {}", message);
        lastAccessTime = message.accessTime;
        dispatcher.tell(new DispatcherActor.UserStateAnswer(message.update, user));
        return Behaviors.withTimers(timer -> {
            timer.startTimerAtFixedRate(TIMER_KEY, new TryExpired(), Duration.ofMinutes(MAX_IDLE_TIME_MINUTES));
            return this;
        });
    }

    private Behavior<StateCommand> onTryExpiredReceive(TryExpired expired) {
        getContext().getLog().debug("Receive TryExpired message: {}", expired);
        if (ChronoUnit.MINUTES.between(lastAccessTime, LocalDateTime.now()) > MAX_IDLE_TIME_MINUTES) {
            getContext().getLog().debug("User with id: {} expired, start to send destroy message", user.getId());
            getContext().getSelf().tell(new DestroyMessage());
        } else {
            getContext().getLog().debug("Not expired, last access time: {}, idle minutes: {}", lastAccessTime, MAX_IDLE_TIME_MINUTES);
        }
        return this;
    }

    public interface StateCommand {
    }

    @Value
    public static class TryExpired implements StateCommand {
    }

    public static class DestroyMessage implements StateCommand {
    }

    @Value
    public static class GetState implements StateCommand {
        LocalDateTime accessTime;
        Update update;
    }
}
