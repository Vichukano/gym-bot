package ru.vichukano.gym.bot.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vichukano.gym.bot.service.UserService;

import static ru.vichukano.gym.bot.actors.BotActor.BotCommand;

public class BotActor extends AbstractBehavior<BotCommand> {
    private final TelegramLongPollingBot bot;
    private final ActorRef<DispatcherActor.DispatcherCommand> dispatcher;

    private BotActor(ActorContext<BotCommand> context, TelegramLongPollingBot bot, UserService userService) {
        super(context);
        this.bot = bot;
        this.dispatcher = getContext().spawn(DispatcherActor.create(getContext().getSelf(), userService), "dispatcher-actor");
    }

    public static Behavior<BotCommand> create(TelegramLongPollingBot bot, UserService service) {
        return Behaviors.setup(ctx -> new BotActor(ctx, bot, service));
    }

    @Override
    public Receive<BotCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(UserCommand.class, this::onUserCommandReceive)
                .onMessage(ReplyMessage.class, this::onReplyMessageReceive)
                .onMessage(ReplyDocument.class, this::onReplyDocumentReceive)
                .build();
    }

    private Behavior<BotCommand> onUserCommandReceive(UserCommand received) {
        getContext().getLog().debug("Receive message: {}", received);
        dispatcher.tell(new DispatcherActor.DispatcherMessage(received.update));
        return this;
    }

    private Behavior<BotCommand> onReplyMessageReceive(ReplyMessage message) {
        getContext().getLog().debug("Receive ReplyMessage: {}", message);
        SendMessage out = message.message;
        try {
            bot.execute(out);
        } catch (TelegramApiException e) {
            getContext().getLog().error("Exception while execute message: {}", message, e);
        }
        return this;
    }

    private Behavior<BotCommand> onReplyDocumentReceive(ReplyDocument document) {
        getContext().getLog().debug("Receive ReplyDocument: {}", document);
        SendDocument out = document.document;
        try {
            bot.execute(out);
        } catch (TelegramApiException e) {
            getContext().getLog().error("Exception while execute message: {}", document, e);
        }
        return this;
    }

    public interface BotCommand {
    }

    @Value
    public static class UserCommand implements BotCommand {
        Update update;
    }

    @Value
    public static class ReplyMessage implements BotCommand {
        SendMessage message;
    }

    @Value
    public static class ReplyDocument implements BotCommand {
        SendDocument document;
    }
}
