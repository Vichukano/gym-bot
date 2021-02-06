package ru.vichukano.trainer.bot.telegram;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@AllArgsConstructor
public class TrainerBot extends TelegramLongPollingBot {
    private final String username;
    private final String token;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received new update: {}", update);
        BotApiMethod<Message> out = new SendMessage();
        log.debug("Out message: {}", out);
        try {
            if (out != null) {
                execute(out);
            }
        } catch (TelegramApiException e) {
            log.error("Exception while handle update: {}", update, e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[botName = %s]", this.getClass().getSimpleName(), username);
    }
}
