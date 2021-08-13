package ru.vichukano.gym.bot.dao.client.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;
import java.time.LocalDateTime;

@Value
@Builder
public class TrainingDto {
    Long id;
    Long userId;
    LocalDateTime trainingDate;
    Duration trainingTime;
}
