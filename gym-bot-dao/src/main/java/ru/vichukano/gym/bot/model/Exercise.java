package ru.vichukano.gym.bot.model;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class Exercise {
    String name;
    List<BigDecimal> weights;
    List<Integer> reps;
}
