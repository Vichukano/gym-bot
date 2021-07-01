package ru.vichukano.gym.bot.actors;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class UserStateActorTest {
    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void testForDifferentUsers() {
        User one = new User("1", "name1", new Training(LocalDateTime.now(), new LinkedList<>()), State.START_TRAINING);
        User two = new User("2", "name2", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        TestProbe<DispatcherActor.DispatcherCommand> probe = testKit.createTestProbe();
        ActorRef<UserStateActor.StateCommand> stateOne = testKit.spawn(UserStateActor.create(probe.getRef(), "1", "name1", State.START_TRAINING), "state-1");
        ActorRef<UserStateActor.StateCommand> stateTwo = testKit.spawn(UserStateActor.create(probe.getRef(), "2", "name2", State.SELECT_EXERCISE), "state-2");
        Update update = ModelFactory.update("test");

        stateOne.tell(new UserStateActor.GetState(LocalDateTime.now(), update));
        stateTwo.tell(new UserStateActor.GetState(LocalDateTime.now(), update));

        probe.expectMessage(Duration.ofSeconds(1), new DispatcherActor.UserStateAnswer(update, one));
        probe.expectMessage(Duration.ofSeconds(1), new DispatcherActor.UserStateAnswer(update, two));
    }

}