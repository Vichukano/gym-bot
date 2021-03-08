package ru.vichukano.gym.bot.handler.document;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.gym.bot.domain.State;
import ru.vichukano.gym.bot.domain.dto.Training;
import ru.vichukano.gym.bot.domain.dto.User;
import ru.vichukano.gym.bot.handler.UpdateHandler;
import ru.vichukano.gym.bot.service.UserService;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;

import static ru.vichukano.gym.bot.store.UserStore.USER_STORE;
import static ru.vichukano.gym.bot.util.MessageUtils.*;

@Slf4j
@AllArgsConstructor
public class SendReportUpdateHandler implements UpdateHandler<SendDocument> {
    private final UserService service;

    @Override
    public SendDocument handle(Update message) {
        log.trace("Handler message: {}", message);
        var out = new SendDocument();
        out.setChatId(chatId(message));
        User user = USER_STORE.USERS.asMap().getOrDefault(
                userId(message),
                new User(userId(message), userName(message), new Training(LocalDateTime.now(), new LinkedList<>()), State.START_TRAINING)
        );
        log.trace("Found user: {}", user);
        File report = service.getTrainingInfo(user);
        if (Objects.nonNull(report)) {
            out.setDocument(new InputFile(report));
        }
        return out;
    }
}
