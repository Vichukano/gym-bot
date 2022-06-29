package ru.vichukano.gym.bot.domain.dto;

import lombok.Data;
import ru.vichukano.gym.bot.domain.State;

import java.util.Objects;

@Data
public class User {
    private final String id;
    private final String name;
    private final Training training;
    private State state;
    private String trainingDescription;
    
    public User(String id, String name, Training training, State state) {
         this.id = id;
         this.name = name;
         this.training = training;
         this.state = state;
         this.trainingDescription = "";
    }

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
