package ru.vichukano.gym.bot.dao.client;

import java.util.Optional;

interface GenericDao<T> {

    Optional<T> findById(Long id) throws Exception;

    void save(T data) throws Exception;

    void update(T data) throws Exception;

}
