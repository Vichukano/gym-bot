package ru.vichukano.gym.bot.actors;

import lombok.val;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ModelFactory {
    public static final long CHAT_ID = 1L;

    public static Update update(String text) {
        val update = new Update();
        val message = new Message();
        val chat = new Chat();
        message.setText(text);
        chat.setId(CHAT_ID);
        message.setChat(chat);
        update.setMessage(message);
        return update;
    }

    public static SendMessage message(String text) {
        val msg = new SendMessage();
        msg.setChatId(String.valueOf(CHAT_ID));
        msg.setText(text);
        return msg;
    }

}
