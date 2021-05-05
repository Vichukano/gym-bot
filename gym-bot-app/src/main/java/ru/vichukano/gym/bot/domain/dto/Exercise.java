package ru.vichukano.gym.bot.domain.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Value
public class Exercise {
    String name;
    LinkedList<BigDecimal> weights = new LinkedList<>();
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
