package ru.vichukano.gym.bot.actors;

import static ru.vichukano.gym.bot.domain.Command.HELP;
import static ru.vichukano.gym.bot.domain.Command.START;
import static ru.vichukano.gym.bot.util.MessageUtils.chatId;
import static ru.vichukano.gym.bot.util.MessageUtils.text;
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
import lombok.val;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;

class StartActor extends AbstractBehavior<StartActor.StartCommand> {
    private static final String START_TEXT = "Send me %s for start training or %s for help.";

    private StartActor(ActorContext<StartCommand> context) {
        super(context);
    }

    public static Behavior<StartCommand> create() {
        return Behaviors.supervise(Behaviors.setup(StartActor::new))
                .onFailure(SupervisorStrategy.restart());
    }

    @Override
    public Receive<StartCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartTraining.class, this::onStartTrainingReceive)
                .build();
    }

    private Behavior<StartCommand> onStartTrainingReceive(StartTraining start) {
        getContext().getLog().debug("Receive message: {}", start);
        val out = new SendMessage();
        Update update = start.update;
        out.setChatId(chatId(update));
        if (START.getCommand().equals(text(update))) {
            out.setText("Choose exercise from:\n");
            out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());
            start.user.setState(State.SELECT_EXERCISE);
        } else {
            out.setText(String.format(START_TEXT, START.getCommand(), HELP.getCommand()));
            out.setReplyMarkup(KeyboardFactory.startKeyboard());
        }
        start.getReplyTo().tell(new BotActor.ReplyMessage(out));
        return this;
    }

    public interface StartCommand {
    }

    @Value
    public static class StartTraining implements StartCommand {
        Update update;
        User user;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
