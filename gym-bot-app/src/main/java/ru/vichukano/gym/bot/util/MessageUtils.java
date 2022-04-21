package ru.vichukano.gym.bot.util;

import java.util.Optional;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class MessageUtils {
    private MessageUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String chatId(Update u) {
        return Optional.ofNullable(u.getMessage())
                .map(Message::getChatId)
                .map(String::valueOf)
                .orElseGet(() -> String.valueOf(u.getCallbackQuery().getMessage().getChatId()));
    }

    public static String text(Update u) {
        return Optional.ofNullable(u.getMessage())
                .map(Message::getText)
                .orElse(null);
    }

    public static String queryData(Update u) {
        return Optional.ofNullable(u.getCallbackQuery())
                .map(CallbackQuery::getData)
                .orElse(null);
    }

    public static String userName(Update u) {
        return Optional.ofNullable(u.getMessage())
                .map(Message::getFrom)
                .map(user -> user.getUserName() != null ? user.getUserName() : user.getFirstName())
                .orElseGet(() -> u.getCallbackQuery().getFrom().getUserName());
    }

    public static String userId(Update u) {
        return Optional.ofNullable(u.getMessage())
                .map(Message::getFrom)
                .map(User::getId)
                .map(String::valueOf)
                .orElseGet(() -> String.valueOf(u.getCallbackQuery().getFrom().getId()));
    }
}
