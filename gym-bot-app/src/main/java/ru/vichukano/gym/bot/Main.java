package ru.vichukano.gym.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vichukano.gym.bot.factory.HandlerFactory;
import ru.vichukano.gym.bot.factory.StateToHandlerFactory;
import ru.vichukano.gym.bot.handler.document.SendReportUpdateHandler;
import ru.vichukano.gym.bot.handler.message.CompoundUpdateHandler;
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
        var stateToHandlerFactory = new StateToHandlerFactory(service);
        var handlerFactory = new HandlerFactory(new CompoundUpdateHandler(stateToHandlerFactory.stateToHandler()), new SendReportUpdateHandler(service));
        new TelegramBotsApi(DefaultBotSession.class).registerBot(new GymBot(handlerFactory, props.getProperty("name"), props.getProperty("token")));
    }

}
