package ru.vichukano.gym.bot.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class Exercise {
    private final String name;
    private BigDecimal weight;
    private Integer reps;
}
