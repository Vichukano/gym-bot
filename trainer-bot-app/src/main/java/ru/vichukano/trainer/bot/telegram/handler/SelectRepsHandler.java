package ru.vichukano.trainer.bot.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;
import ru.vichukano.trainer.bot.fsm.state.State;

@Slf4j
@AllArgsConstructor
public class SelectRepsHandler implements BotMessageHandler<Update, SendMessage> {
    private final State state;

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        long chatId = update.getMessage().getChatId();
        SendMessage out = new SendMessage();
        out.setChatId(String.valueOf(chatId));
        String reps = update.getMessage().getText();
        log.info("Selected reps: {}", reps);
        //TODO: Нужно взять айди юзера и сохранить вес в крайнее упражнение
        out.setText("Вы ввели повторений" + reps);
        return out;
    }
}
