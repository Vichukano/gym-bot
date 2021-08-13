package ru.vichukano.gym.bot.dao.client.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ExerciseDto {
    Long id;
    Long trainingId;
    String name;
    Integer weight;
    Integer reps;
    LocalDateTime createDate;
}
