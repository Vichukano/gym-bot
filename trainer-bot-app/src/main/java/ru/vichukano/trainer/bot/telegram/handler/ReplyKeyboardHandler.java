package ru.vichukano.trainer.bot.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;

import java.util.List;

public class ReplyKeyboardHandler implements BotMessageHandler<Update, SendMessage> {

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            return null;
        }
        Message msg = update.getMessage();
        if ("YES".equals(msg.getText()) || "NO".equals(msg.getText())) {
            var out = new SendMessage();
            out.setChatId(String.valueOf(msg.getChatId()));
            out.setText("Вы нажали кнопку " + msg.getText());
            return out;
        }
        var replyKb = new ReplyKeyboardMarkup();
        replyKb.setSelective(true);
        replyKb.setResizeKeyboard(true);
        replyKb.setOneTimeKeyboard(false);
        var firstRow = new KeyboardRow();
        var secondRow = new KeyboardRow();
        firstRow.add("YES");
        secondRow.add("NO");
        replyKb.setKeyboard(List.of(firstRow, secondRow));
        var out = new SendMessage();
        out.setChatId(String.valueOf(msg.getChatId()));
        out.setReplyMarkup(replyKb);
        out.setText("Выберите");
        return out;
    }

}
