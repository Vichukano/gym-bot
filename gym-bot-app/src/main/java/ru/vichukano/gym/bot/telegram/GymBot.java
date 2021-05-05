package ru.vichukano.gym.bot.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.handler.UpdateHandler;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class GymBot extends TelegramLongPollingBot {
    private final UpdateHandler<Object> handlerFactory;
    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received new update: {}", update);
        try {
            Object out = handlerFactory.handle(update);
            log.debug("Out message: {}", out);
            if (Objects.nonNull(out) && out instanceof SendMessage) {
                execute((SendMessage) out);
            } else if (Objects.nonNull(out) && out instanceof SendDocument) {
                execute((SendDocument) out);
            }
        } catch (Exception e) {
            log.error("Exception while handle update: {}", update, e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[botName = %s]", this.getClass().getSimpleName(), botUsername);
    }
}
