package ru.vichukano.gym.bot.dao.client;

import ru.vichukano.gym.bot.dao.client.dto.ExerciseDto;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface ExerciseDao extends GenericDao<ExerciseDto> {

    Collection<ExerciseDto> findByTrainingId(Long trainingId) throws Exception;

    Optional<ExerciseDto> findLastByTrainingId(Long trainingId) throws Exception;

}
