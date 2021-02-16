package ru.vichukano.gym.bot.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vichukano.gym.bot.handler.MessageHandler;

@Slf4j
@AllArgsConstructor
public class GymBot extends TelegramLongPollingBot {
    private final MessageHandler handler;
    @Getter
    private final String botUsername;
    @Getter
    private final String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received new update: {}", update);
        try {
            SendMessage out = handler.handle(update);
            log.debug("Out message: {}", out);
            if (out != null) {
                execute(out);
            }
        } catch (TelegramApiException e) {
            log.error("Exception while handle update: {}", update, e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[botName = %s]", this.getClass().getSimpleName(), botUsername);
    }
}
