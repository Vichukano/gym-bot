package ru.vichukano.trainer.bot.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;
import ru.vichukano.trainer.bot.fsm.state.State;

@Slf4j
@AllArgsConstructor
public class SelectSexHandler implements BotMessageHandler<Update, SendMessage> {
    private final State state;

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        Message msg = update.getMessage();
        SendMessage out = state.answer();
        out.setChatId(String.valueOf(msg.getChatId()));
        if ("M".equals(msg.getText())) {
            log.info("Create male user with id: {}", msg.getFrom().getId());
            return out;
        } else if ("F".equals(msg.getText())) {
            log.info("Create female user with id: {}", msg.getFrom().getId());
            return out;
        }
        return null;
    }

}
