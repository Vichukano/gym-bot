package ru.vichukano.gym.bot.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.service.UserDatabaseService;

import static ru.vichukano.gym.bot.actors.DatabaseSaverActor.SaveCommand;

public class DatabaseSaverActor extends AbstractBehavior<SaveCommand> {
    private final UserDatabaseService databaseService;

    private DatabaseSaverActor(ActorContext<SaveCommand> context, UserDatabaseService databaseService) {
        super(context);
        this.databaseService = databaseService;
    }

    public static Behavior<SaveCommand> create(UserDatabaseService databaseService) {
        return Behaviors.<SaveCommand>supervise(Behaviors.setup(ctx -> new DatabaseSaverActor(ctx, databaseService)))
                .onFailure(SupervisorStrategy.restart());
    }

    @Override
    public Receive<SaveCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(SaveUserInfoCommand.class, this::onSaveUserInfoCommand)
                .build();
    }

    private Behavior<SaveCommand> onSaveUserInfoCommand(SaveUserInfoCommand command) {
        databaseService.saveUserInfo(command.getUser());
        return this;
    }

    public interface SaveCommand {

    }

    @Value
    public class SaveUserInfoCommand implements SaveCommand {
        User user;
    }
}
