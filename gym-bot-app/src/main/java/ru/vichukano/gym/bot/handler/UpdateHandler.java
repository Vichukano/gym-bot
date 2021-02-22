package ru.vichukano.gym.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Interface of update handlers
 *
 * @param <T> type of response for telegram
 */
public interface UpdateHandler<T> {

    T handle(Update message);

}
