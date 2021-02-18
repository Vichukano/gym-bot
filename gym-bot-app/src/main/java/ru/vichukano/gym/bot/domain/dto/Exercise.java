package ru.vichukano.gym.bot.domain.dto;

import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Value
public class Exercise {
    String name;
    List<BigDecimal> weights = new ArrayList<>();
    List<Integer> reps = new ArrayList<>();

    @Override
    public String toString() {
        return name
                + ", weight: "
                + weights
                + ", reps: "
                + reps;
    }
}
