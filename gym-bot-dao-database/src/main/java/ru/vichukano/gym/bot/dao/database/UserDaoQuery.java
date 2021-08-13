package ru.vichukano.gym.bot.dao.database;

import lombok.extern.slf4j.Slf4j;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;
import ru.vichukano.gym.bot.dao.client.UserDao;
import ru.vichukano.gym.bot.dao.client.dto.UserDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Slf4j
public class UserDaoQuery implements UserDao {
    private static final String FIND_USER_BY_ID = "SELECT * FROM GYM_BOT_USER gbu WHERE gbu.id = ?;";
    private static final String SAVE_USER = "INSERT INTO GYM_BOT_USER(ID, NAME, NICK_NAME, CREATE_DATE, WEIGHT, HEIGHT, LANGUAGE)"
            + " VALUES(?, ?, ?, ?, ?, ?, ?);";
    private static final String UPDATE_USER = "UPDATE GYM_BOT_USER SET NAME = ?, NICK_NAME = ?, UPDATE_DATE = ?," +
            " WEIGHT = ?, HEIGHT = ?, LANGUAGE =? WHERE ID = ?;";
    private final DatabaseConnection dbConnection;

    public UserDaoQuery(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Optional<UserDto> findById(Long id) throws SQLException {
        log.trace("Try to query user by id: {}", id);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_USER_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                UserDto user = null;
                while (rs.next()) {
                    user = UserDto.builder()
                            .id(rs.getLong("ID"))
                            .name(rs.getString("NAME"))
                            .nickName(rs.getString("NICK_NAME"))
                            .createDate(fromDate(rs.getString("CREATE_DATE")))
                            .updateDate(fromDate(rs.getString("UPDATE_DATE")))
                            .weight(rs.getInt("WEIGHT"))
                            .height(rs.getInt("HEIGHT"))
                            .language(UserDto.Language.valueOf(rs.getString("LANGUAGE")))
                            .build();

                }
                log.trace("Found user: {}", user);
                return Optional.ofNullable(user);
            }
        }
    }

    @Override
    public void save(UserDto data) throws SQLException {
        log.trace("Try to save new user: {}", data);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(SAVE_USER)) {
            ps.setLong(1, data.getId());
            ps.setString(2, data.getName());
            ps.setString(3, data.getNickName());
            ps.setString(4, data.getCreateDate().format(ISO_LOCAL_DATE_TIME));
            ps.setInt(5, data.getWeight());
            ps.setInt(6, data.getHeight());
            ps.setString(7, data.getLanguage().name());
            int rows = ps.executeUpdate();
            log.trace("Rows updated: {}", rows);
        }
    }

    @Override
    public void update(UserDto data) throws SQLException {
        log.trace("Try to update user: {}", data);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(UPDATE_USER)) {
            ps.setString(1, data.getName());
            ps.setString(2, data.getNickName());
            ps.setString(3, data.getUpdateDate().format(ISO_LOCAL_DATE_TIME));
            ps.setInt(4, data.getWeight());
            ps.setInt(5, data.getHeight());
            ps.setString(6, data.getLanguage().name());
            ps.setLong(7, data.getId());
            int updatedRows = ps.executeUpdate();
            log.trace("Updated rows: {}", updatedRows);
        }
    }

    private LocalDateTime fromDate(String date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME);
    }
}
