package ru.vichukano.gym.bot.factory;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.handler.UpdateHandler;
import ru.vichukano.gym.bot.handler.message.*;
import ru.vichukano.gym.bot.service.UserService;

import java.util.EnumMap;
import java.util.Map;

@AllArgsConstructor
public class StateToHandlerFactory {
    private final UserService service;

    public Map<State, UpdateHandler<SendMessage>> stateToHandler() {
        return new EnumMap<>(Map.of(
                State.START_TRAINING, new StartUpdateHandler(),
                State.SELECT_EXERCISE, new SelectExerciseHandler(),
                State.SELECT_WEIGHT, new SelectWeightUpdateHandler(),
                State.SELECT_REPS, new SelectRepsUpdateHandler(),
                State.STOP, new StopUpdateHandler(service))
        );
    }
}
