package ru.vichukano.gym.bot.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Value
public class Training {
    LocalDateTime time;
    @Getter
    LinkedList<Exercise> exercises;
}
