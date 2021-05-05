package ru.vichukano.gym.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vichukano.gym.bot.domain.Command;
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

    public static ReplyKeyboardMarkup startKeyboard() {
        var markup = new ReplyKeyboardMarkup();
        markup.setOneTimeKeyboard(true);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        var start = new KeyboardRow();
        start.add(Command.START.getCommand());
        var help = new KeyboardRow();
        help.add(Command.HELP.getCommand());
        markup.setKeyboard(List.of(start, help));
        return markup;
    }

    public static ReplyKeyboardMarkup afterSetKeyboard() {
        var markup = new ReplyKeyboardMarkup();
        markup.setOneTimeKeyboard(true);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        var stop = new KeyboardRow();
        stop.add(Command.STOP.getCommand());
        var exercise = new KeyboardRow();
        exercise.add(Command.EXERCISE.getCommand());
        var rows = new ArrayList<KeyboardRow>();
        rows.add(stop);
        rows.add(exercise);
        markup.setKeyboard(rows);
        return markup;
    }

    public static ReplyKeyboardMarkup zeroWeightButton() {
        var markup = new ReplyKeyboardMarkup();
        markup.setOneTimeKeyboard(true);
        markup.setSelective(true);
        markup.setResizeKeyboard(true);
        var zero = new KeyboardRow();
        zero.add("0");
        markup.setKeyboard(List.of(zero));
        return markup;
    }

}
