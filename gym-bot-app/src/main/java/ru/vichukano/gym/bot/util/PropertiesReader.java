package ru.vichukano.gym.bot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class PropertiesReader {

    private PropertiesReader() {
        throw new IllegalStateException("Utility class");
    }

    public static Properties loadFromCp(String name) {
        var props = new Properties();
        try (var in = PropertiesReader.class.getClassLoader().getResourceAsStream(name)) {
            props.load(in);
        } catch (IOException e) {
            log.error("Can't load properties form: {}", name, e);
        }
        return props;
    }

    public static Properties loadFromArgs(String fileName) {
        var props = new Properties();
        try (var in = new FileInputStream(fileName)) {
            props.load(in);
        } catch (IOException e) {
            log.error("Can't load properties form: {}", fileName, e);
        }
        return props;
    }

}
