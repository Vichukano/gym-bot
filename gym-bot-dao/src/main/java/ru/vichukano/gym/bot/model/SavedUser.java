package ru.vichukano.gym.bot.model;

import lombok.Value;

import java.util.List;

@Value
public class SavedUser {
    String id;
    String name;
    List<Training> trainings;
}
