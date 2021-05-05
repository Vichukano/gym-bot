package ru.vichukano.gym.bot.store.listener;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.extern.slf4j.Slf4j;
import ru.vichukano.gym.bot.domain.dto.User;

@Slf4j
public class UserRemovalListener implements RemovalListener<String, User> {

    @Override
    public void onRemoval(RemovalNotification<String, User> notification) {
        User user = notification.getValue();
        //TODO: прикртить сохранине записи о тренировк
        log.info("User {} removed from cache", user);
    }

}
