package ru.vichukano.gym.bot.actors;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Exercise;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.factory.KeyboardFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static ru.vichukano.gym.bot.domain.Exercise.BENCH_PRESS;

public class CancelActorTest {
    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void whenReceiveCancelMessageThenUndoLastExerciseAndAnswer() {
        ActorRef<CancelActor.CancelCommand> testTarget = testKit.spawn(CancelActor.create(), "cancel-actor");
        Exercise exercise1 = new Exercise(BENCH_PRESS.name());
        exercise1.getWeights().add(new BigDecimal("100"));
        exercise1.getReps().add(10);
        Exercise exercise2 = new Exercise(BENCH_PRESS.name());
        exercise2.getWeights().add(new BigDecimal("150"));
        LinkedList<Exercise> exercises = new LinkedList<>();
        exercises.add(exercise1);
        exercises.add(exercise2);
        User user = new User("1", "test", new Training(LocalDateTime.now(), exercises), State.SELECT_WEIGHT);
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        Update update = ModelFactory.update("");
        SendMessage out = new SendMessage();
        out.setChatId(String.valueOf(update.getMessage().getChatId()));
        out.setText("Successfully undo exercise, input new exercise form:\n");
        out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());

        testTarget.tell(new CancelActor.CancelExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_EXERCISE);
        assertEquals(1, user.getTraining().getExercises().size());
        assertEquals(new BigDecimal("100"), user.getTraining().getExercises().get(0).getWeights().get(0));
    }

}
