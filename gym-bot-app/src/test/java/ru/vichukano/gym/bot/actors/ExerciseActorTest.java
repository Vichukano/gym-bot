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

import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.junit.Assert.*;
import static ru.vichukano.gym.bot.domain.Command.CANCEL;
import static ru.vichukano.gym.bot.domain.Exercise.*;

public class ExerciseActorTest {
    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void testForBenchPress() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(BENCH_PRESS.getCommand());
        SendMessage out = ModelFactory.message("Start to bench. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-bench-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(BENCH_PRESS.name())));
    }

    @Test
    public void testForSquat() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(SQUAT.getCommand());
        SendMessage out = ModelFactory.message("Start to squat. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-squat-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(SQUAT.name())));
    }

    @Test
    public void testForDeadLift() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(DEAD_LIFT.getCommand());
        SendMessage out = ModelFactory.message("Start to lift. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-lift-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(DEAD_LIFT.name())));
    }

    @Test
    public void testForOverheadPress() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(OVERHEAD_PRESS.getCommand());
        SendMessage out = ModelFactory.message("Start to overhead press. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-overhead-press-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(OVERHEAD_PRESS.name())));
    }

    @Test
    public void testForDumbbellsOverheadPress() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(DUMBBELLS_OVERHEAD_PRESS.getCommand());
        SendMessage out = ModelFactory.message("Start to overhead dumbbells press. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-dumbbells-overhead-press-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(DUMBBELLS_OVERHEAD_PRESS.name())));
    }

    @Test
    public void testForDumbbellsBenchPress() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(DUMBBELLS_BENCH_PRESS.getCommand());
        SendMessage out = ModelFactory.message("Start to dumbbells bench press. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-dumbbells-bench-press-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(DUMBBELLS_BENCH_PRESS.name())));
    }

    @Test
    public void testForAbc() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(ABS.getCommand());
        SendMessage out = ModelFactory.message("Start to abs. Input weight in KG. If you do with body weigh, than input 0 or " + CANCEL.getCommand() + " for undo");
        out.setReplyMarkup(KeyboardFactory.zeroWeightButton());
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-abs-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(ABS.name())));
    }

    @Test
    public void testForPullUps() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(PULL_UP.getCommand());
        SendMessage out = ModelFactory.message("Start to pull ups. Input weight in KG. If you do it with body weight, than input 0 or " + CANCEL.getCommand() + " for undo");
        out.setReplyMarkup(KeyboardFactory.zeroWeightButton());
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-pull-up-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(PULL_UP.name())));
    }

    @Test
    public void testForPushOnBars() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(PUSH_UP_ON_BARS.getCommand());
        SendMessage out = ModelFactory.message("Start to push ups on bars. Input weight in KG. If you do it with body weight, than input 0 or " + CANCEL.getCommand() + " for undo");
        out.setReplyMarkup(KeyboardFactory.zeroWeightButton());
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-push-on-bars-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(PUSH_UP_ON_BARS.name())));
    }

    @Test
    public void testForUnknownCommand() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update("unknown");
        SendMessage out = ModelFactory.message("Input correct exercise form:\n");
        out.setReplyMarkup(KeyboardFactory.exercisesKeyboard());
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-unknown-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_EXERCISE);
        assertEquals(0, user.getTraining().getExercises().size());
    }
    
    @Test
    public void testForBarbellBicepsCurl() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(BARBELL_BECEPS_CURL.getCommand());
        SendMessage out = ModelFactory.message("Start to barbell biceps curls. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-barbell-biceps-curl-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(BARBELL_BECEPS_CURL.name())));
    }
    
    @Test
    public void testForDumbellBicepsCurl() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(DUMBBELLS_BICEPS_CURL.getCommand());
        SendMessage out = ModelFactory.message("Start to dumbbells biceps curls. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-dumbbells-biceps-curl-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(DUMBBELLS_BICEPS_CURL.name())));
    }
    
    @Test
    public void testForHackSquatMachime() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(HACK_SQUAT_MACHINE.getCommand());
        SendMessage out = ModelFactory.message("Start to hack squat machine. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-hack-squat-machine-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(HACK_SQUAT_MACHINE.name())));
    }
    
    @Test
    public void testForLegPressMachine() {
        User user = new User("id", "name", new Training(LocalDateTime.now(), new LinkedList<>()), State.SELECT_EXERCISE);
        Update update = ModelFactory.update(LEG_PRESS_MACHINE.getCommand());
        SendMessage out = ModelFactory.message("Start to leg press machine. Input weight in KG or " + CANCEL.getCommand() + " for undo");
        TestProbe<BotActor.BotCommand> probe = testKit.createTestProbe();
        ActorRef<ExerciseActor.ExerciseCommand> testTarget = testKit.spawn(ExerciseActor.create(), "exercise-leg-precc-machine-actor");

        testTarget.tell(new ExerciseActor.SelectExercise(update, user, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
        assertSame(user.getState(), State.SELECT_WEIGHT);
        assertTrue(user.getTraining().getExercises().stream().map(Exercise::getName).anyMatch(n -> n.equals(LEG_PRESS_MACHINE.name())));
    }
}
