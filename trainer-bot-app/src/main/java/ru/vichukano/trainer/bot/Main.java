package ru.vichukano.trainer.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vichukano.trainer.bot.config.AppConfigDto;
import ru.vichukano.trainer.bot.fsm.BotStateMachine;
import ru.vichukano.trainer.bot.resources.ConfigFactory;
import ru.vichukano.trainer.bot.telegram.TrainerBot;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

@Slf4j
public class Main {

    public static void main(String[] args) throws TelegramApiException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        log.debug("Starting bot!");
        AppConfigDto config = new ConfigFactory<AppConfigDto>().getConfigDto(AppConfigDto.class);
        var tba = new TelegramBotsApi(DefaultBotSession.class);
        tba.registerBot(new TrainerBot(config.getBotName(), config.getBotToken(), new BotStateMachine(new HashMap<>())));
    }

}
