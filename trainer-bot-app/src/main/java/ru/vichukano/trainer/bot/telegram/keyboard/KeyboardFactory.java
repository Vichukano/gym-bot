package ru.vichukano.trainer.bot.telegram.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardFactory {

    public ReplyKeyboardMarkup selectSexKeyboard() {
        var replyKb = new ReplyKeyboardMarkup();
        replyKb.setSelective(true);
        replyKb.setResizeKeyboard(true);
        replyKb.setOneTimeKeyboard(true);
        var firstRow = new KeyboardRow();
        var secondRow = new KeyboardRow();
        firstRow.add("M");
        secondRow.add("F");
        replyKb.setKeyboard(List.of(firstRow, secondRow));
        return replyKb;
    }

    public ReplyKeyboardMarkup startTrainingKeyboard() {
        var replyKb = new ReplyKeyboardMarkup();
        replyKb.setSelective(true);
        replyKb.setResizeKeyboard(true);
        replyKb.setOneTimeKeyboard(false);
        var firstRow = new KeyboardRow();
        firstRow.add("Начать тренировку");
        replyKb.setKeyboard(List.of(firstRow));
        return replyKb;
    }

    public ReplyKeyboardMarkup selectExerciseKeyboard() {
        var replyKb = new ReplyKeyboardMarkup();
        replyKb.setSelective(true);
        replyKb.setResizeKeyboard(true);
        replyKb.setOneTimeKeyboard(false);
        var firstRow = new KeyboardRow();
        var secondRow = new KeyboardRow();
        var thirdRow = new KeyboardRow();
        var exit = new KeyboardRow();
        firstRow.add("Жим лежа");
        secondRow.add("Присед");
        thirdRow.add("Становая тяга");
        exit.add("Завершить тренировку");
        replyKb.setKeyboard(List.of(firstRow, secondRow, thirdRow, exit));
        return replyKb;
    }

}
