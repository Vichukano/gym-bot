package ru.vichukano.gym.bot.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@UtilityClass
public class PropertiesReader {

    public static Properties load(String name) {
        var props = new Properties();
        try (var in = PropertiesReader.class.getClassLoader().getResourceAsStream(name)) {
            props.load(in);
        } catch (IOException e) {
            log.error("Can't load properties form: {}", name, e);
        }
        return props;
    }

}
