package ru.vichukano.gym.bot.actors;

import static ru.vichukano.gym.bot.domain.Command.START;
import static ru.vichukano.gym.bot.util.MessageUtils.chatId;
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
import ru.vichukano.gym.bot.actors.HelpActor.HelpCommand;

class HelpActor extends AbstractBehavior<HelpCommand> {
    private static final String HELP_TEXT = "Hi! I am a gym training bot, I can help to track your progress in the gym."
            + " Type %s for start your training session."
            + " You can choose exercises from list, set reps and weight,"
            + " and after training session I show your training report.";

    private HelpActor(ActorContext<HelpCommand> context) {
        super(context);
    }

    public static Behavior<HelpCommand> create() {
        return Behaviors.supervise(Behaviors.setup(HelpActor::new))
                .onFailure(SupervisorStrategy.restart());
    }

    @Override
    public Receive<HelpCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(HelpCommand.class, this::onReceive)
                .build();
    }

    private Behavior<HelpCommand> onReceive(HelpCommand message) {
        getContext().getLog().debug("Receive message: {}", message);
        var out = new SendMessage();
        String chatId = chatId(message.update);
        out.setChatId(chatId);
        out.setText(String.format(HELP_TEXT, START.getCommand()));
        message.getReplyTo().tell(new BotActor.ReplyMessage(out));
        return this;
    }

    @Value
    public static class HelpCommand {
        Update update;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
