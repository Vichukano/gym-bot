package ru.vichukano.gym.bot.actors;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.ClassRule;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static ru.vichukano.gym.bot.domain.Command.START;

public class HelpActorTest {
    @ClassRule
    public static final TestKitJunitResource TEST_KIT = new TestKitJunitResource();

    @Test
    public void whenReceiveHelpMessageThenAnswer() {
        ActorRef<HelpActor.HelpCommand> testTarget = TEST_KIT.spawn(HelpActor.create(), "help-actor");
        TestProbe<BotActor.BotCommand> probe = TEST_KIT.createTestProbe();
        Update update = ModelFactory.update("");
        SendMessage out = ModelFactory.message("Hi! I am a gym training bot, I can help to track your progress in the gym."
                + " Type " + START.getCommand() + " for start your training session."
                + " You can choose exercises from list, set reps and weight,"
                + " and after training session I show your training report.");

        testTarget.tell(new HelpActor.HelpCommand(update, probe.getRef()));

        probe.expectMessage(new BotActor.ReplyMessage(out));
    }

}