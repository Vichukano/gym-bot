package ru.vichukano.gym.bot.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vichukano.gym.bot.domain.State;

import java.util.Objects;

@Data
@AllArgsConstructor
public class User {
    private final String id;
    private final String name;
    private final Training training;
    private State state;

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
