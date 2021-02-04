package ru.vichukano.telegram.bot.internal.api.dao;

import java.util.Collection;

public interface GenericDao<K, V> {

    V getOne(K key);

    Collection<V> getAll(K key);

}
