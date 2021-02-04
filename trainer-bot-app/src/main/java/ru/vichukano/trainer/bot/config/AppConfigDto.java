package ru.vichukano.trainer.bot.config;

import lombok.Getter;
import ru.vichukano.trainer.bot.resources.annotation.Property;
import ru.vichukano.trainer.bot.resources.annotation.Resource;

@Resource(path = "/home/vichukano/programming/java/config/config.properties")
public class AppConfigDto {
    @Getter
    @Property(name = "bot.name")
    private String botName;
    @Getter
    @Property(name = "bot.token")
    private String botToken;
}
