package ru.vichukano.gym.bot.dao.client.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class UserDto {
    Long id;
    String name;
    String nickName;
    LocalDateTime createDate;
    LocalDateTime updateDate;
    Integer weight;
    Integer height;
    Language language;

    public enum Language {
        RUS, ENG
    }
}
