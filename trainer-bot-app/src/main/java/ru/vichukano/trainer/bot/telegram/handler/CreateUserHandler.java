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
public class CreateUserHandler implements BotMessageHandler<Update, SendMessage> {
    private final State state;

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        Message msg = update.getMessage();
        SendMessage out = state.answer();
        out.setChatId(String.valueOf(msg.getChatId()));
        return out;
    }

}
