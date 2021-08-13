package ru.vichukano.gym.bot.dao.database;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;
import ru.vichukano.gym.bot.dao.client.dto.TrainingDto;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static org.junit.Assert.*;

@Slf4j
public class TrainingDaoQueryTest {
    private static final String DELETE = "DELETE FROM GYM_BOT_TRAINING;";
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
        var query = new TrainingDaoQuery(CON);
        var training = TrainingDto.builder()
                .id(1L)
                .userId(2L)
                .trainingDate(LocalDateTime.now())
                .trainingTime(Duration.ofMinutes(30))
                .build();
        query.save(training);
        CON.commit();
        var found = query.findById(training.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), training);
    }

    @Test
    public void shouldSuccessfullyUpdateAndFindById() throws SQLException {
        var query = new TrainingDaoQuery(CON);
        var training = TrainingDto.builder()
                .id(1L)
                .userId(2L)
                .trainingDate(LocalDateTime.now())
                .trainingTime(Duration.ofMinutes(30))
                .build();
        query.save(training);
        CON.commit();
        var forUpdate = TrainingDto.builder()
                .id(1L)
                .userId(2L)
                .trainingDate(training.getTrainingDate())
                .trainingTime(Duration.ofMinutes(60))
                .build();
        query.update(forUpdate);
        CON.commit();
        var found = query.findById(training.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), forUpdate);
    }

    @Test
    public void shouldSuccessfullyFindByUserId() throws SQLException {
        var query = new TrainingDaoQuery(CON);
        var trainingOne = TrainingDto.builder()
                .id(1L)
                .userId(2L)
                .trainingDate(LocalDateTime.now())
                .trainingTime(Duration.ofMinutes(30))
                .build();
        query.save(trainingOne);
        var trainingTwo = TrainingDto.builder()
                .id(2L)
                .userId(2L)
                .trainingDate(LocalDateTime.now())
                .trainingTime(Duration.ofMinutes(65))
                .build();
        query.save(trainingTwo);
        CON.commit();
        Collection<TrainingDto> found = query.findByUserId(trainingOne.getUserId());
        assertFalse(found.isEmpty());
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(t -> t.equals(trainingOne)));
        assertTrue(found.stream().anyMatch(t -> t.equals(trainingTwo)));
    }

    @Test
    public void shouldSuccessfullyFindLastByUserId() throws SQLException {
        var query = new TrainingDaoQuery(CON);
        var trainingOne = TrainingDto.builder()
                .id(1L)
                .userId(2L)
                .trainingDate(LocalDateTime.now().minusDays(1))
                .trainingTime(Duration.ofMinutes(30))
                .build();
        query.save(trainingOne);
        var trainingTwo = TrainingDto.builder()
                .id(2L)
                .userId(2L)
                .trainingDate(LocalDateTime.now())
                .trainingTime(Duration.ofMinutes(65))
                .build();
        query.save(trainingTwo);
        CON.commit();
        Optional<TrainingDto> found = query.findLastByUserId(trainingOne.getUserId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), trainingTwo);
    }

    @Test
    public void shouldSuccessfullyFindInRangeByUserId() throws SQLException {
        var query = new TrainingDaoQuery(CON);
        var trainingOne = TrainingDto.builder()
                .id(1L)
                .userId(2L)
                .trainingDate(LocalDateTime.now().minusDays(1))
                .trainingTime(Duration.ofMinutes(30))
                .build();
        query.save(trainingOne);
        var trainingTwo = TrainingDto.builder()
                .id(2L)
                .userId(2L)
                .trainingDate(LocalDateTime.now().plusDays(1))
                .trainingTime(Duration.ofMinutes(65))
                .build();
        query.save(trainingTwo);
        CON.commit();
        Collection<TrainingDto> found = query.findInRangeByUserId(trainingOne.getUserId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2));
        assertFalse(found.isEmpty());
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(t -> t.equals(trainingOne)));
        assertTrue(found.stream().anyMatch(t -> t.equals(trainingTwo)));
    }
}
