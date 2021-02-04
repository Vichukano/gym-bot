package ru.vichukano.telegram.bot.internal.api.fsm;

import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;

public interface HandlerProvider<S, T, V> {

    BotMessageHandler<T, V> handlerByState(S state);

}
