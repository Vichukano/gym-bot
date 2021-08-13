package ru.vichukano.gym.bot.dao.client;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection extends AutoCloseable {

    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

}
