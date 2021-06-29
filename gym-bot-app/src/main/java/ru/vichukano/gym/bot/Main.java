package ru.vichukano.gym.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vichukano.gym.bot.service.UserService;
import ru.vichukano.gym.bot.telegram.GymBot;
import ru.vichukano.gym.bot.util.PropertiesReader;

import java.util.Properties;

@Slf4j
public class Main {

    public static void main(String[] args) throws TelegramApiException {
        log.debug("Starting bot!");
        if (args.length < 1) {
            throw new IllegalArgumentException("Path to configuration not specified. Set path with argument");
        }
        String pathToConfig = args[0];
        log.debug("Path to config: {}", pathToConfig);
        Properties props = PropertiesReader.loadFromArgs(pathToConfig);
        var service = new UserService(new UserExcelDao(props.getProperty("store").isEmpty() ? System.getProperty("java.io.tmpdir") : props.getProperty("store")));
        var bot = new GymBot(props.getProperty("name"), props.getProperty("token"), service);
        new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
    }

}
