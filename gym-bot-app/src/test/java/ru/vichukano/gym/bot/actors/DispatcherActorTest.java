package ru.vichukano.gym.bot.actors;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.vichukano.gym.bot.domain.Command;
import ru.vichukano.gym.bot.factory.KeyboardFactory;
import ru.vichukano.gym.bot.service.UserService;

import java.time.Duration;

import static ru.vichukano.gym.bot.domain.Command.*;

public class DispatcherActorTest {
    @ClassRule
    public static final TestKitJunitResource TEST_KIT = new TestKitJunitResource();

    @AfterClass
    public static void reset() {
        TEST_KIT.system().terminate();
    }

    @Test
    public void testHelpCommand() {
        UserService mockService = Mockito.mock(UserService.class);
        TestProbe<BotActor.BotCommand> probe = TEST_KIT.createTestProbe();
        ActorRef<DispatcherActor.DispatcherCommand> testTarget = TEST_KIT.spawn(DispatcherActor.create(probe.getRef(), mockService), "test-1");
        Update update = withUser(HELP.getCommand(), 1, "name");
        SendMessage out = ModelFactory.message("Hi! I am a gym training bot, I can help to track your progress in the gym."
                + " Type " + START.getCommand() + " for start your training session."
                + " You can choose exercises from list, set reps and weight,"
                + " and after training session I show your training report.");

        testTarget.tell(new DispatcherActor.DispatcherMessage(update));

        probe.expectMessage(Duration.ofSeconds(1), new BotActor.ReplyMessage(out));
    }

    @Test
    public void testStartCommand() {
        UserService mockService = Mockito.mock(UserService.class);
        TestProbe<BotActor.BotCommand> probe = TEST_KIT.createTestProbe();
        ActorRef<DispatcherActor.DispatcherCommand> testTarget = TEST_KIT.spawn(DispatcherActor.create(probe.getRef(), mockService), "test-2");
        Update update = withUser(START.getCommand(), 1, "name");
        SendMessage out = ModelFactory.message("Choose exercise from:\n");
        out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());

        testTarget.tell(new DispatcherActor.DispatcherMessage(update));

        probe.expectMessage(Duration.ofSeconds(1), new BotActor.ReplyMessage(out));
    }

    @Test
    public void testCancelCommand() {
        UserService mockService = Mockito.mock(UserService.class);
        TestProbe<BotActor.BotCommand> probe = TEST_KIT.createTestProbe();
        ActorRef<DispatcherActor.DispatcherCommand> testTarget = TEST_KIT.spawn(DispatcherActor.create(probe.getRef(), mockService), "test-3");
        Update update = withUser(CANCEL.getCommand(), 1, "name");
        SendMessage out = ModelFactory.message("Successfully undo exercise, input new exercise form:\n");
        out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());

        testTarget.tell(new DispatcherActor.DispatcherMessage(update));

        probe.expectMessage(Duration.ofSeconds(1), new BotActor.ReplyMessage(out));
    }

    @Test
    public void testStopCommand() {
        UserService mockService = Mockito.mock(UserService.class);
        TestProbe<BotActor.BotCommand> probe = TEST_KIT.createTestProbe();
        ActorRef<DispatcherActor.DispatcherCommand> testTarget = TEST_KIT.spawn(DispatcherActor.create(probe.getRef(), mockService), "test-4");
        Update update = withUser(STOP.getCommand(), 1, "name");
        SendMessage out = ModelFactory.message("Successfully undo exercise, input new exercise form:\n");
        out.setText("Stop training. Your results:\n"
                + "Training session time: "
                + Duration.ofMinutes(0).toMinutes()
                + " minutes"
                + "\nExercises:\n"
                + "\ntype "
                + Command.REPORT.getCommand()
                + " for send training report."
        );

        testTarget.tell(new DispatcherActor.DispatcherMessage(update));

        probe.expectMessage(Duration.ofSeconds(1), new BotActor.ReplyMessage(out));
        Mockito.verify(mockService, Mockito.times(1)).saveUserTrainingInfo(Mockito.any());
    }

    private Update withUser(String text, int id, String name) {
        Update update = ModelFactory.update(text);
        User u = new User();
        u.setId(id);
        u.setUserName(name);
        update.getMessage().setFrom(u);
        return update;
    }
}