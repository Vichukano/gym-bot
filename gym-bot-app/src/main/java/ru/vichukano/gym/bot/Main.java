package ru.vichukano.gym.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vichukano.gym.bot.factory.StateToHandlerFactory;
import ru.vichukano.gym.bot.handler.CompoundMessageHandler;
import ru.vichukano.gym.bot.telegram.GymBot;
import ru.vichukano.gym.bot.util.PropertiesReader;

import java.util.Properties;

@Slf4j
public class Main {

    public static void main(String[] args) throws TelegramApiException {
        log.debug("Starting bot!");
        Properties props = PropertiesReader.load("app.properties");
        var factory = new StateToHandlerFactory();
        new TelegramBotsApi(DefaultBotSession.class)
                .registerBot(
                        new GymBot(
                                new CompoundMessageHandler(factory.stateToHandler()),
                                props.getProperty("name"),
                                props.getProperty("token")
                        )
                );
    }

}
