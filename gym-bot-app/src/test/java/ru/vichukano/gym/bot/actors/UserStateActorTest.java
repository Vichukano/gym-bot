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
    public static final TestKitJunitResource TEST_KIT = new TestKitJunitResource();

    @Test
    public void testForDifferentUsers() {
        User one = new User("10", "name1", new Training(LocalDateTime.now(), new LinkedList<>()), State.START_TRAINING);
        User two = new User("20", "name2", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        TestProbe<DispatcherActor.DispatcherCommand> probe = TEST_KIT.createTestProbe();
        ActorRef<UserStateActor.StateCommand> stateOne = TEST_KIT.spawn(UserStateActor.create(probe.getRef(), one.getId(), one.getName(), one.getState()), "state-1");
        ActorRef<UserStateActor.StateCommand> stateTwo = TEST_KIT.spawn(UserStateActor.create(probe.getRef(), two.getId(), two.getName(), two.getState()), "state-2");
        Update update = ModelFactory.update("test");

        stateOne.tell(new UserStateActor.GetState(LocalDateTime.now(), update));
        probe.expectMessage(Duration.ofSeconds(2), new DispatcherActor.UserStateAnswer(update, one));
        stateTwo.tell(new UserStateActor.GetState(LocalDateTime.now(), update));
        probe.expectMessage(Duration.ofSeconds(2), new DispatcherActor.UserStateAnswer(update, two));
    }

}