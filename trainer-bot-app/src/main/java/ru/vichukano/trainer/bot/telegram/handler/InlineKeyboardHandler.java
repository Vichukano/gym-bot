package ru.vichukano.trainer.bot.telegram.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class InlineKeyboardHandler implements BotMessageHandler<Update, SendMessage> {

    @Override
    public SendMessage handle(Update update) {
        if (!update.hasMessage() && !update.getMessage().hasText()) {
            return null;
        }
        Message msg = update.getMessage();
        log.info("Handle text message: {}", msg);
        var out = new SendMessage();
        out.setChatId(String.valueOf(msg.getChatId()));
        out.setText(new StringBuilder(msg.getText()).reverse().toString());
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var btn1 = new InlineKeyboardButton();
        var btn2 = new InlineKeyboardButton();
        btn1.setText("Button 1");
        btn1.setCallbackData("btn_1");
        btn2.setText("Button 2");
        btn2.setCallbackData("btn_2");
        rowInline.add(btn1);
        rowInline.add(btn2);
        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        out.setReplyMarkup(markupInline);
        return out;
    }

}
