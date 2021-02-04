package ru.vichukano.trainer.bot.fsm;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;
import ru.vichukano.trainer.bot.fsm.state.State;

import java.util.Map;
import java.util.Optional;

import static ru.vichukano.trainer.bot.fsm.state.State.FINISH_TRAINING;

@Slf4j
@AllArgsConstructor
public class BotStateMachine implements BotMessageHandler<Update, SendMessage> {
    private final Map<Integer, State> userStates;

    @Override
    public SendMessage handle(Update update) {
        Integer userId = getUserId(update);
        if (update.hasMessage() && "Завершить тренировку".equals(update.getMessage().getText())) {
            SendMessage message = FINISH_TRAINING.getHandler().handle(update);
            userStates.put(userId, FINISH_TRAINING.next());
            return message;
        }
        State state = userStates.getOrDefault(userId, State.START);
        log.info("Selected state: {}", state);
        SendMessage message = state.getHandler().handle(update);
        if (message != null) {
            log.info("Before put userId: {} and state: {}", userId, state.next());
            userStates.put(userId, state.next());
        }
        return message;
    }

    private Integer getUserId(Update update) {
        return Optional.ofNullable(update)
                .map(Update::getMessage)
                .map(Message::getFrom)
                .map(User::getId)
                .or(() -> Optional.ofNullable(update)
                        .map(Update::getCallbackQuery)
                        .map(CallbackQuery::getFrom)
                        .map(User::getId))
                .orElseThrow(() -> new IllegalStateException("User id not presented in update"));
    }
}
