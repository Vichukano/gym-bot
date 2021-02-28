package ru.vichukano.gym.bot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vichukano.gym.bot.dao.google.disk.UserGoogleDiskDao;
import ru.vichukano.gym.bot.factory.HandlerFactory;
import ru.vichukano.gym.bot.factory.StateToHandlerFactory;
import ru.vichukano.gym.bot.handler.document.SendReportUpdateHandler;
import ru.vichukano.gym.bot.handler.message.CompoundUpdateHandler;
import ru.vichukano.gym.bot.service.UserService;
import ru.vichukano.gym.bot.telegram.GymBot;
import ru.vichukano.gym.bot.util.PropertiesReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class Main {
    private static final String APPLICATION_NAME = "Gym Bot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final Set<String> SCOPES = DriveScopes.all();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static void main(String[] args) throws TelegramApiException, GeneralSecurityException, IOException {
        log.debug("Starting bot!");
        Properties props = PropertiesReader.load("app.properties");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        var service = new UserService(
                new UserGoogleDiskDao(
                        driveService,
                        props.getProperty("store").isEmpty() ? System.getProperty("java.io.tmpdir") : props.getProperty("store")
                )
        );
        var stateToHandlerFactory = new StateToHandlerFactory(service);
        var handlerFactory = new HandlerFactory(
                new CompoundUpdateHandler(stateToHandlerFactory.stateToHandler()),
                new SendReportUpdateHandler(service)
        );
        new TelegramBotsApi(DefaultBotSession.class)
                .registerBot(
                        new GymBot(
                                handlerFactory,
                                props.getProperty("name"),
                                props.getProperty("token")
                        )
                );
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = Main.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("Vichukano");
    }

}
