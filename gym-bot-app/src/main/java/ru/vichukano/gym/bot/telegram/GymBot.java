package ru.vichukano.gym.bot.telegram;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.actors.BotActor;
import ru.vichukano.gym.bot.service.UserService;

@Slf4j
public class GymBot extends TelegramLongPollingBot {
    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;
    private final ActorRef<BotActor.BotCommand> botActor;

    public GymBot(String botUsername, String botToken, UserService userService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botActor = ActorSystem.create(BotActor.create(this, userService), "main-bot-actor");
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received new update: {}", update);
        try {
            botActor.tell(new BotActor.UserCommand(update));
        } catch (Exception e) {
            log.error("Exception while handle update: {}", update, e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[botName = %s]", this.getClass().getSimpleName(), botUsername);
    }
}
