package ru.vichukano.trainer.bot.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;
import ru.vichukano.trainer.bot.fsm.state.State;

@Slf4j
@AllArgsConstructor
public class StartTrainingHandler implements BotMessageHandler<Update, SendMessage> {
    private final State state;

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage() && !"Начать тренировку".equals(update.getMessage().getText())) {
            return null;
        }
        long chatId = update.getMessage().getChatId();
        SendMessage out = state.answer();
        out.setChatId(String.valueOf(chatId));
        return out;
    }

}
