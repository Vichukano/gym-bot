package ru.vichukano.gym.bot.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.service.UserService;

import java.io.File;
import java.util.Objects;

import static ru.vichukano.gym.bot.util.MessageUtils.chatId;

public class ReportActor extends AbstractBehavior<ReportActor.ReportCommand> {
    private final UserService userService;

    private ReportActor(ActorContext<ReportCommand> context, UserService userService) {
        super(context);
        this.userService = userService;
    }

    public static Behavior<ReportCommand> create(UserService service) {
        return Behaviors.setup(ctx -> new ReportActor(ctx, service));
    }

    @Override
    public Receive<ReportCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TrainingReport.class, this::onTrainingReportReceive)
                .build();
    }

    private Behavior<ReportCommand> onTrainingReportReceive(TrainingReport report) {
        getContext().getLog().debug("Receive message: {}", report);
        Update update = report.update;
        ActorRef<BotActor.BotCommand> replyTo = report.replyTo;
        var out = new SendDocument();
        out.setChatId(chatId(update));
        User user = report.user;
        File reportFile = null;
        try {
            reportFile = userService.getTrainingInfo(user);
        } catch (Exception e) {
            getContext().getLog().error("Exception while getting file report for user: {}", user);
        }
        if (Objects.nonNull(reportFile)) {
            out.setDocument(new InputFile(reportFile));
        }
        replyTo.tell(new BotActor.ReplyDocument(out));
        return this;
    }

    public interface ReportCommand {
    }

    @Value
    public static class TrainingReport implements ReportCommand {
        Update update;
        User user;
        ActorRef<BotActor.BotCommand> replyTo;
    }
}
