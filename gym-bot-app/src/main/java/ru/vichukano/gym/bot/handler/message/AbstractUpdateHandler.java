package ru.vichukano.gym.bot.handler.message;

import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.handler.UpdateHandler;

import static ru.vichukano.gym.bot.util.MessageUtils.chatId;

public abstract class AbstractUpdateHandler implements UpdateHandler<SendMessage> {

    @Override
    public SendMessage handle(Update message) {
        log().debug("Handler message: {}", message);
        var out = new SendMessage();
        out.setChatId(chatId(message));
        return out;
    }

    protected abstract Logger log();

}
