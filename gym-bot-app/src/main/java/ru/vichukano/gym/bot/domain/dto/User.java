package ru.vichukano.gym.bot.domain.dto;

import lombok.Getter;
import lombok.Value;

import java.util.List;
import java.util.Objects;

@Value
public class User {
    String id;
    String name;
    Training training;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
