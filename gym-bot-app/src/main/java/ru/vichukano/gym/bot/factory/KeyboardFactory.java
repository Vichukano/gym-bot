package ru.vichukano.gym.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vichukano.gym.bot.domain.Exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class KeyboardFactory {

    private KeyboardFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static InlineKeyboardMarkup exercisesKeyboard() {
        var markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = Arrays.stream(Exercise.values()).map(e -> {
            var btn = new InlineKeyboardButton();
            btn.setText(e.getDesc());
            btn.setCallbackData(e.getCommand());
            var row = new ArrayList<InlineKeyboardButton>();
            row.add(btn);
            return row;
        }).collect(Collectors.toList());
        markup.setKeyboard(buttons);
        return markup;
    }

}
