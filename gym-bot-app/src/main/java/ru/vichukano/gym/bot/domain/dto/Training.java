package ru.vichukano.gym.bot.domain.dto;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Value
public class Training {
    LocalDateTime time;
    LinkedList<Exercise> exercises;
}
