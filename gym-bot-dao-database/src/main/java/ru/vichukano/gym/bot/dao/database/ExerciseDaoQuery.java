package ru.vichukano.gym.bot.dao.database;

import lombok.extern.slf4j.Slf4j;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;
import ru.vichukano.gym.bot.dao.client.ExerciseDao;
import ru.vichukano.gym.bot.dao.client.dto.ExerciseDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Slf4j
public class ExerciseDaoQuery implements ExerciseDao {
    private static final String FIND_EXERCISES_BY_TRAINING_ID = "SELECT * FROM GYM_BOT_EXERCISE WHERE TRAINING_ID = ?;";
    private static final String FIND_LAST_EXERCISE_BY_TRAINING_ID = "SELECT * FROM GYM_BOT_EXERCISE WHERE TRAINING_ID = ?"
            + " ORDER BY DATETIME(CREATE_DATE) DESC LIMIT 1;";
    private static final String FIND_EXERCISE_BY_ID = "SELECT * FROM GYM_BOT_EXERCISE WHERE ID = ?;";
    private static final String SAVE_EXERCISE = "INSERT INTO GYM_BOT_EXERCISE(ID, TRAINING_ID, NAME, WEIGHT, REPS, CREATE_DATE)"
            + " VALUES(?, ?, ?, ?, ?, ?);";
    private static final String UPDATE_EXERCISE = "UPDATE GYM_BOT_EXERCISE SET NAME = ?, WEIGHT = ?, REPS = ? WHERE ID =?;";
    private final DatabaseConnection dbConnection;

    public ExerciseDaoQuery(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Collection<ExerciseDto> findByTrainingId(Long trainingId) throws SQLException {
        log.trace("Try to query exercises by training id: {}", trainingId);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_EXERCISES_BY_TRAINING_ID)) {
            ps.setLong(1, trainingId);
            try (ResultSet rs = ps.executeQuery()) {
                List<ExerciseDto> exercises = new ArrayList<>();
                while (rs.next()) {
                    exercises.add(
                            ExerciseDto.builder()
                                    .id(rs.getLong("ID"))
                                    .trainingId(rs.getLong("TRAINING_ID"))
                                    .name(rs.getString("NAME"))
                                    .weight(rs.getInt("WEIGHT"))
                                    .reps(rs.getInt("REPS"))
                                    .createDate(LocalDateTime.parse(rs.getString("CREATE_DATE"), ISO_LOCAL_DATE_TIME))
                                    .build()
                    );
                }
                log.trace("Count of found exercises: {}", exercises.size());
                return exercises;
            }
        }
    }

    @Override
    public Optional<ExerciseDto> findLastByTrainingId(Long trainingId) throws SQLException {
        log.trace("Try to query last exercise by training id: {}", trainingId);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_LAST_EXERCISE_BY_TRAINING_ID)) {
            ps.setLong(1, trainingId);
            try (ResultSet rs = ps.executeQuery()) {
                ExerciseDto exercise = null;
                while (rs.next()) {
                    exercise = ExerciseDto.builder()
                            .id(rs.getLong("ID"))
                            .trainingId(rs.getLong("TRAINING_ID"))
                            .name(rs.getString("NAME"))
                            .weight(rs.getInt("WEIGHT"))
                            .reps(rs.getInt("REPS"))
                            .createDate(LocalDateTime.parse(rs.getString("CREATE_DATE"), ISO_LOCAL_DATE_TIME))
                            .build();
                }
                log.trace("Found exercise: {}", exercise);
                return Optional.ofNullable(exercise);
            }
        }
    }

    @Override
    public Optional<ExerciseDto> findById(Long id) throws SQLException {
        log.trace("Try to query exercise by id: {}", id);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(FIND_EXERCISE_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                ExerciseDto exercise = null;
                while (rs.next()) {
                    exercise = ExerciseDto.builder()
                            .id(rs.getLong("ID"))
                            .trainingId(rs.getLong("TRAINING_ID"))
                            .name(rs.getString("NAME"))
                            .weight(rs.getInt("WEIGHT"))
                            .reps(rs.getInt("REPS"))
                            .createDate(LocalDateTime.parse(rs.getString("CREATE_DATE"), ISO_LOCAL_DATE_TIME))
                            .build();
                }
                log.trace("Found exercise: {}", exercise);
                return Optional.ofNullable(exercise);
            }
        }
    }

    @Override
    public void save(ExerciseDto data) throws SQLException {
        log.trace("Try to save new exercise: {}", data);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(SAVE_EXERCISE)) {
            ps.setLong(1, data.getId());
            ps.setLong(2, data.getTrainingId());
            ps.setString(3, data.getName());
            ps.setInt(4, data.getWeight());
            ps.setInt(5, data.getReps());
            ps.setString(6, ISO_LOCAL_DATE_TIME.format(data.getCreateDate()));
            int rows = ps.executeUpdate();
            log.trace("Rows updated: {}", rows);
        }
    }

    @Override
    public void update(ExerciseDto data) throws SQLException {
        log.trace("Try to update exercise: {}", data);
        Connection con = dbConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(UPDATE_EXERCISE)) {
            ps.setString(1, data.getName());
            ps.setInt(2, data.getWeight());
            ps.setInt(3, data.getReps());
            ps.setLong(4, data.getId());
            int updatedRows = ps.executeUpdate();
            log.trace("Updated rows: {}", updatedRows);
        }
    }
}
