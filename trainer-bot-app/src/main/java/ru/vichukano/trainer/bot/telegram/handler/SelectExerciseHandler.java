package ru.vichukano.trainer.bot.telegram.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;
import ru.vichukano.trainer.bot.fsm.state.State;

@Slf4j
@AllArgsConstructor
public class SelectExerciseHandler implements BotMessageHandler<Update, SendMessage> {
    private final State state;

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        String chatId = String.valueOf(update.getMessage().getChatId());
        Integer userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();
        var out = new SendMessage();
        out.setChatId(chatId);
        if ("Жим лежа".equals(text)) {
            out.setText("Bench press select weight");
        } else if ("Присед".equals(text)) {
            out.setText("Squat select weight");
        } else if ("Становая тяга".equals(text)) {
            out.setText("Deadlift select weight");
        } else {
            out.setText("NONE");
        }
        //TODO: Здесь нужно сохранять в базу упражнение с привязкой к юзеру
        log.info("User with id: {} select exercise: {}", userId, text);
        return out;
    }

}
