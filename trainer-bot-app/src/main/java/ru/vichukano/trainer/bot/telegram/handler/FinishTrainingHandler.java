package ru.vichukano.trainer.bot.telegram.handler;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;

import java.util.List;

@Slf4j
public class FinishTrainingHandler implements BotMessageHandler<Update, SendMessage> {

    @Override
    public SendMessage handle(Update update) {
        if (update.hasMessage() && "Завершить тренировку".equals(update.getMessage().getText())) {
            log.debug("Запрос на завершение тренирвоки");
            String chatId = String.valueOf(update.getMessage().getChatId());
            var out = new SendMessage();
            out.setChatId(chatId);
            out.setText("Вы закончили тренировку.");
            out.setReplyMarkup(createKeyboard());
            return out;
        }
        return null;
    }

    private ReplyKeyboardMarkup createKeyboard() {
        var replyKb = new ReplyKeyboardMarkup();
        replyKb.setSelective(true);
        replyKb.setResizeKeyboard(true);
        replyKb.setOneTimeKeyboard(false);
        var firstRow = new KeyboardRow();
        firstRow.add("Начать тренировку");
        replyKb.setKeyboard(List.of(firstRow));
        return replyKb;
    }
}
