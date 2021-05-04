package ru.vichukano.gym.bot.factory;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.handler.UpdateHandler;
import ru.vichukano.gym.bot.util.MessageUtils;

import static ru.vichukano.gym.bot.domain.Command.REPORT;

@Slf4j
@AllArgsConstructor
public class HandlerFactory implements UpdateHandler<Object> {
    private final UpdateHandler<SendMessage> compoundMsgHandler;
    private final UpdateHandler<SendDocument> compoundDocHandler;

    @Override
    public Object handle(Update message) {
        if (!validMessage(message) && !validCallback(message)) {
            //TODO: Может лучше кидать исключения???
            log.warn("Receive null");
            return null;
        }
        if (REPORT.getCommand().equals(MessageUtils.text(message))) {
            log.debug("Start handle for document handler: {}", message);
            return compoundDocHandler.handle(message);
        }
        log.debug("Start handle for sendMessage handler: {}", message);
        return compoundMsgHandler.handle(message);
    }

    private boolean validMessage(Update message) {
        return message.hasMessage() && message.getMessage().hasText();
    }

    private boolean validCallback(Update message) {
        return message.hasCallbackQuery();
    }
}
