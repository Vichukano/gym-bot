package ru.vichukano.gym.bot.model;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class Training {
    LocalDateTime time;
    String description;
    List<Exercise> exercises;
}
