package ru.vichukano.gym.bot.dao.database;

import lombok.extern.slf4j.Slf4j;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;
import ru.vichukano.gym.bot.dao.client.TrainingDao;
import ru.vichukano.gym.bot.dao.client.dto.TrainingDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Slf4j
public class TrainingDaoQuery implements TrainingDao {
    private static final String FIND_TRAINING_BY_ID = "SELECT * FROM GYM_BOT_TRAINING WHERE ID = ?;";
    private static final String SAVE_TRAINING = "INSERT INTO GYM_BOT_TRAINING(ID, USER_ID, TRAINING_DATE, TRAINING_TIME)"
            + " VALUES (?, ?, ?, ?);";
    private static final String UPDATE_TRAINING = "UPDATE GYM_BOT_TRAINING SET TRAINING_DATE = ?, TRAINING_TIME = ?"
            + " WHERE ID = ?;";
    private static final String FIND_TRAININGS_BY_USER_ID = "SELECT * FROM GYM_BOT_TRAINING WHERE USER_ID = ?;";
    private static final String FIND_LAST_TRAINING_BY_USER_ID = "SELECT * FROM GYM_BOT_TRAINING WHERE USER_ID = ?"
            + " ORDER BY DATETIME(TRAINING_DATE) DESC LIMIT 1;";
    private static final String FIND_TRAININGS_IN_RANGE_BY_USER_ID = "SELECT * FROM GYM_BOT_TRAINING WHERE USER_ID =?"
            + " AND (DATETIME(TRAINING_DATE) BETWEEN DATETIME(?) AND DATETIME(?));";
    private final DatabaseConnection dbConnection;

    public TrainingDaoQuery(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Optional<TrainingDto> findById(Long id) throws SQLException {
        log.trace("Try to query training by id: {}", id);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_TRAINING_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                TrainingDto training = null;
                while (rs.next()) {
                    training = TrainingDto.builder()
                            .id(rs.getLong("ID"))
                            .userId(rs.getLong("USER_ID"))
                            .trainingDate(LocalDateTime.parse(rs.getString("TRAINING_DATE"), ISO_LOCAL_DATE_TIME))
                            .trainingTime(rs.getString("TRAINING_TIME") != null ? Duration.parse(rs.getString("TRAINING_TIME")) : null)
                            .build();
                }
                log.trace("Found training: {}", training);
                return Optional.ofNullable(training);
            }
        }
    }

    @Override
    public void save(TrainingDto data) throws SQLException {
        log.trace("Try to save new training: {}", data);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(SAVE_TRAINING)) {
            ps.setLong(1, data.getId());
            ps.setLong(2, data.getUserId());
            ps.setString(3, data.getTrainingDate().format(ISO_LOCAL_DATE_TIME));
            ps.setString(4, data.getTrainingTime().toString());
            int rows = ps.executeUpdate();
            log.trace("Rows updated: {}", rows);
        }
    }

    @Override
    public void update(TrainingDto data) throws SQLException {
        log.trace("Try to update training: {}", data);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(UPDATE_TRAINING)) {
            ps.setString(1, data.getTrainingDate().format(ISO_LOCAL_DATE_TIME));
            if (Objects.nonNull(data.getTrainingTime())) {
                ps.setString(2, data.getTrainingTime().toString());
            }
            ps.setLong(3, data.getId());
            int updatedRows = ps.executeUpdate();
            log.trace("Updated rows: {}", updatedRows);
        }
    }

    @Override
    public Collection<TrainingDto> findByUserId(Long userId) throws SQLException {
        log.trace("Try to query trainings by user id: {}", userId);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_TRAININGS_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<TrainingDto> trainings = new ArrayList<>();
                while (rs.next()) {
                    trainings.add(
                            TrainingDto.builder()
                                    .id(rs.getLong("ID"))
                                    .userId(rs.getLong("USER_ID"))
                                    .trainingDate(LocalDateTime.parse(rs.getString("TRAINING_DATE"), ISO_LOCAL_DATE_TIME))
                                    .trainingTime(rs.getString("TRAINING_TIME") != null ? Duration.parse(rs.getString("TRAINING_TIME")) : null)
                                    .build()
                    );
                }
                log.trace("Count of found trainings: {}", trainings.size());
                return trainings;
            }
        }
    }

    @Override
    public Optional<TrainingDto> findLastByUserId(Long userId) throws SQLException {
        log.trace("Try to query last training by user id: {}", userId);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_LAST_TRAINING_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                TrainingDto training = null;
                while (rs.next()) {
                    training = TrainingDto.builder()
                            .id(rs.getLong("ID"))
                            .userId(rs.getLong("USER_ID"))
                            .trainingDate(LocalDateTime.parse(rs.getString("TRAINING_DATE"), ISO_LOCAL_DATE_TIME))
                            .trainingTime(rs.getString("TRAINING_TIME") != null ? Duration.parse(rs.getString("TRAINING_TIME")) : null)
                            .build();
                }
                log.trace("Found training: {}", training);
                return Optional.ofNullable(training);
            }
        }
    }

    @Override
    public Collection<TrainingDto> findInRangeByUserId(Long userId, LocalDateTime start, LocalDateTime end) throws SQLException {
        log.trace("Try to query trainings by user id: {} and range {} - {}", userId, start, end);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_TRAININGS_IN_RANGE_BY_USER_ID)) {
            ps.setLong(1, userId);
            ps.setString(2, start.format(ISO_LOCAL_DATE_TIME));
            ps.setString(3, end.format(ISO_LOCAL_DATE_TIME));
            try (ResultSet rs = ps.executeQuery()) {
                List<TrainingDto> trainings = new ArrayList<>();
                while (rs.next()) {
                    trainings.add(
                            TrainingDto.builder()
                                    .id(rs.getLong("ID"))
                                    .userId(rs.getLong("USER_ID"))
                                    .trainingDate(LocalDateTime.parse(rs.getString("TRAINING_DATE"), ISO_LOCAL_DATE_TIME))
                                    .trainingTime(rs.getString("TRAINING_TIME") != null ? Duration.parse(rs.getString("TRAINING_TIME")) : null)
                                    .build()
                    );
                }
                log.trace("Count of found trainings:{} in range {} - {}", trainings.size(), start, end);
                return trainings;
            }
        }
    }
}
