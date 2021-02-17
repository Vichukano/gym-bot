package ru.vichukano.gym.bot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;

@UtilityClass
public class MessageUtils {

    public static String chatId(Update u) {
        return String.valueOf(u.getMessage().getChatId());
    }

    public static String text(Update u) {
        return u.getMessage().getText();
    }

    public static String userName(Update u) {
        return u.getMessage().getFrom().getUserName();
    }

    public static String userId(Update u) {
        return String.valueOf(u.getMessage().getFrom().getId());
    }

}
