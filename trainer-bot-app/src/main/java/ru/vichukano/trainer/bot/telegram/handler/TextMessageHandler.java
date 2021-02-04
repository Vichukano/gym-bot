package ru.vichukano.trainer.bot.telegram.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;

@Slf4j
public class TextMessageHandler implements BotMessageHandler<Update, SendMessage> {

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            return null;
        }
        Message msg = update.getMessage();
        log.info("Handle text message: {}", msg);
        var out = new SendMessage();
        out.setChatId(String.valueOf(msg.getChatId()));
        out.setText(new StringBuilder(msg.getText()).reverse().toString());
        return out;
    }
}
