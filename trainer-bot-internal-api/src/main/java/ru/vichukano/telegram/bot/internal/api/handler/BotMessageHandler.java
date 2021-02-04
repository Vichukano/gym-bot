package ru.vichukano.telegram.bot.internal.api.handler;

/**
 * Interface of bot received message handler.
 *
 * @param <T> type of input message from bot for handling
 * @param <V> type of output message to bot after handling
 */
public interface BotMessageHandler<T, V> {

    V handle(T message);

}
