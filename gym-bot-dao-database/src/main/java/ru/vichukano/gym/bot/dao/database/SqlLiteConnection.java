package ru.vichukano.gym.bot.dao.database;

import lombok.extern.slf4j.Slf4j;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class SqlLiteConnection implements DatabaseConnection {
    private final Properties properties;
    private Connection connection;

    public SqlLiteConnection(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (Objects.isNull(connection)) {
            String url = properties.getProperty("db-url");
            log.trace("Start to create new connection for url: {}", url);
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);
            log.trace("Successfully create new connection: {}", connection);
            return connection;
        }
        log.trace("Connection already exists: {}", connection);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (Objects.nonNull(connection)) {
            log.trace("Start to commit transaction");
            connection.commit();
            log.trace("Transaction successfully committed");
        }
    }

    @Override
    public void close() throws Exception {
        if (Objects.nonNull(connection)) {
            log.trace("Start to close connection: {}", connection);
            connection.close();
            log.trace("Connection successfully closed");
        }
    }
}
