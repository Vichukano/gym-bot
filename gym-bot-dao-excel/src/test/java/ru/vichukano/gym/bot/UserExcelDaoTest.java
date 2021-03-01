package ru.vichukano.gym.bot;

import org.junit.Ignore;
import org.junit.Test;
import ru.vichukano.gym.bot.model.Exercise;
import ru.vichukano.gym.bot.model.SavedUser;
import ru.vichukano.gym.bot.model.Training;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UserExcelDaoTest {
    private static final String TEMP = System.getProperty("java.io.tmpdir");

    static SavedUser model() {
        var bench = new Exercise(
                "BENCH_PRESS",
                List.of(new BigDecimal(100), new BigDecimal(120), new BigDecimal(120)),
                List.of(10, 8, 8)
        );
        var squat = new Exercise(
                "SQUAT",
                List.of(new BigDecimal(200), new BigDecimal(210)),
                List.of(6, 4)
        );
        var lift = new Exercise(
                "DEAD_LIFT",
                List.of(new BigDecimal(300), new BigDecimal(310), new BigDecimal(400)),
                List.of(4, 4, 3)
        );
        var training = new Training(LocalDateTime.now(), List.of(bench, squat, lift));
        return new SavedUser("1232", "test", List.of(training));
    }

    @Ignore
    @Test
    public void shouldSaveWithoutExceptions() {
        var testTarget = new UserExcelDao(TEMP);
        System.out.println("Temp dir: " + TEMP);
        try {
            testTarget.saveOrUpdate(model());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Ignore
    @Test
    public void shouldGetNotEmpty() {
        var testTarget = new UserExcelDao(TEMP);
        var user = model();
        try {
            testTarget.saveOrUpdate(user);
            Optional<File> file = testTarget.getByFileName(user.getId() + user.getName());
            assertTrue(file.isPresent());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}