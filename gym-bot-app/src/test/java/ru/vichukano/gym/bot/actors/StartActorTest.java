package ru.vichukano.gym.bot.actors;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;

import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.junit.Assert.assertSame;
import static ru.vichukano.gym.bot.domain.Command.HELP;
import static ru.vichukano.gym.bot.domain.Command.START;

public class StartActorTest {
    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void whenSendStartTrainingCommandThenReceiveMessageWithExercisesListAndChangeUserState() {
        ActorRef<StartActor.StartCommand> testTarget = testKit.spawn(StartActor.create(), "start-actor");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        Update update = ModelFactory.update(Command.START.getCommand());
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_WEIGHT);
        SendMessage out = ModelFactory.message("Choose exercise from:\n");
        out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());

        testTarget.tell(new StartActor.StartTraining(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(State.SELECT_EXERCISE, user.getState());
    }

    @Test
    public void whenSendStartTrainingCommandWitchInvalidTextThenReceiveMessageWithDefaultText() {
        ActorRef<StartActor.StartCommand> testTarget = testKit.spawn(StartActor.create(), "start1-actor");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        Update update = ModelFactory.update("invalid text");
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_WEIGHT);
        SendMessage out = ModelFactory.message("Send me "
                + START.getCommand()
                + " for start training"
                + " or "
                + HELP.getCommand()
                + " for help.");
        out.setReplyMarkup(KeyboardFactory.startKeyboard());

        testTarget.tell(new StartActor.StartTraining(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(State.SELECT_WEIGHT, user.getState());
    }

}