package ru.vichukano.trainer.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vichukano.trainer.bot.telegram.TrainerBot;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class Main {

    public static void main(String[] args) throws TelegramApiException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        log.debug("Starting bot!");
        new TelegramBotsApi(DefaultBotSession.class).
                registerBot(new TrainerBot("name", "token"));
    }

}
