package ru.vichukano.gym.bot.factory;

import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.handler.*;

import java.util.EnumMap;
import java.util.Map;

public class StateToHandlerFactory {

    public Map<State, MessageHandler> stateToHandler() {
        return new EnumMap<>(Map.of(State.START_TRAINING, new StartMessageHandler(),
                State.SELECT_EXERCISE, new SelectExerciseHandler(),
                State.SELECT_WEIGHT, new SelectWeightMessageHandler(),
                State.SELECT_REPS, new SelectRepsMessageHandler(),
                State.STOP, new StopMessageHandler()));
    }
}
