package ru.vichukano.gym.bot.dao.client;

import ru.vichukano.gym.bot.dao.client.dto.TrainingDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface TrainingDao extends GenericDao<TrainingDto> {

    Collection<TrainingDto> findByUserId(Long userId) throws Exception;

    Optional<TrainingDto> findLastByUserId(Long userId) throws Exception;

    Collection<TrainingDto> findInRangeByUserId(Long userId, LocalDateTime start, LocalDateTime end) throws Exception;

}
