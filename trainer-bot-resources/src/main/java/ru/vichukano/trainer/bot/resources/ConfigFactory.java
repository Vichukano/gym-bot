package ru.vichukano.trainer.bot.resources;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.vichukano.trainer.bot.resources.annotation.Property;
import ru.vichukano.trainer.bot.resources.annotation.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class ConfigFactory<T> {

    public T getConfigDto(@NonNull Class<T> clazz) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Resource resource = clazz.getAnnotation(Resource.class);
        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException("@Resource annotation not set for type: " + clazz);
        }
        T configDto = clazz.getDeclaredConstructor().newInstance();
        Properties props = getProperties(resource.path());
        for (Field field : clazz.getDeclaredFields()) {
            if (Objects.nonNull(field.getAnnotation(Property.class))) {
                Property p = field.getAnnotation(Property.class);
                field.setAccessible(true);
                field.set(configDto, props.getProperty(p.name()));
            }
        }
        log.trace("Configuration DTO successfully created. Type: {}", clazz);
        return configDto;
    }

    private Properties getProperties(@NonNull String path) {
        var props = new Properties();
        try (final InputStream is = new FileInputStream(path)) {
            props.load(is);
            log.trace("Properties successfully loaded. Size: {}", props.size());
            return props;
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read properties from path: " + path);
        }
    }

}
