package ru.vichukano.gym.bot.dao.database;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;
import ru.vichukano.gym.bot.dao.client.dto.ExerciseDto;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class ExerciseDaoQueryTest {
    private static final String DELETE = "DELETE FROM GYM_BOT_EXERCISE;";
    private static final Properties PROPS = new Properties();
    private static final DatabaseConnection CON = new SqlLiteConnection(PROPS);

    static {
        PROPS.setProperty("db-url", "jdbc:sqlite:./src/test/resources/test.db");
    }

    @AfterClass
    public static void closeConnection() throws Exception {
        CON.close();
        log.info("Connection: {} successfully closed", CON);
    }

    @After
    public void reset() throws Exception {
        try (Statement st = CON.getConnection().createStatement()) {
            final int deleted = st.executeUpdate(DELETE);
            CON.commit();
            log.info("Successfully clear database, deleted rows: {}", deleted);
        }
    }

    @Test
    public void shouldSuccessfullySaveAndFindById() throws SQLException {
        var query = new ExerciseDaoQuery(CON);
        var exercise = ExerciseDto.builder()
                .id(1L)
                .trainingId(2L)
                .name("BENCH")
                .weight(100)
                .reps(10)
                .createDate(LocalDateTime.now())
                .build();
        query.save(exercise);
        CON.commit();
        Optional<ExerciseDto> found = query.findById(exercise.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), exercise);
    }

    @Test
    public void shouldSuccessfullyUpdate() throws SQLException {
        var query = new ExerciseDaoQuery(CON);
        var exercise = ExerciseDto.builder()
                .id(1L)
                .trainingId(2L)
                .name("BENCH")
                .weight(100)
                .reps(10)
                .createDate(LocalDateTime.now())
                .build();
        query.save(exercise);
        CON.commit();
        var forUpdate = ExerciseDto.builder()
                .id(1L)
                .trainingId(2L)
                .name("DEAD_LIFT")
                .weight(200)
                .reps(8)
                .createDate(exercise.getCreateDate())
                .build();
        query.update(forUpdate);
        Optional<ExerciseDto> found = query.findById(exercise.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), forUpdate);
    }

    @Test
    public void shouldSuccessfullyFindByTrainingId() throws SQLException {
        var query = new ExerciseDaoQuery(CON);
        var exerciseFirst = ExerciseDto.builder()
                .id(1L)
                .trainingId(2L)
                .name("BENCH")
                .weight(100)
                .reps(10)
                .createDate(LocalDateTime.now())
                .build();
        var exerciseSecond = ExerciseDto.builder()
                .id(2L)
                .trainingId(2L)
                .name("SQUAT")
                .weight(250)
                .reps(8)
                .createDate(LocalDateTime.now())
                .build();
        query.save(exerciseFirst);
        query.save(exerciseSecond);
        CON.commit();
        Collection<ExerciseDto> found = query.findByTrainingId(exerciseFirst.getTrainingId());
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(e -> e.equals(exerciseFirst)));
        assertTrue(found.stream().anyMatch(e -> e.equals(exerciseSecond)));
    }

    @Test
    public void shouldSuccessfullyFindLastByTrainingId() throws SQLException {
        var query = new ExerciseDaoQuery(CON);
        var exerciseFirst = ExerciseDto.builder()
                .id(1L)
                .trainingId(2L)
                .name("BENCH")
                .weight(100)
                .reps(10)
                .createDate(LocalDateTime.now().minusHours(1))
                .build();
        var exerciseSecond = ExerciseDto.builder()
                .id(2L)
                .trainingId(2L)
                .name("SQUAT")
                .weight(250)
                .reps(8)
                .createDate(LocalDateTime.now())
                .build();
        query.save(exerciseFirst);
        query.save(exerciseSecond);
        CON.commit();
        Optional<ExerciseDto> found = query.findLastByTrainingId(exerciseFirst.getTrainingId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), exerciseSecond);
    }

}