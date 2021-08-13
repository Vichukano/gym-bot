package ru.vichukano.gym.bot.dao.database;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import ru.vichukano.gym.bot.dao.client.DatabaseConnection;
import ru.vichukano.gym.bot.dao.client.dto.UserDto;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;

@Slf4j
public class UserDaoQueryTest {
    private static final String DELETE = "DELETE FROM GYM_BOT_USER;";
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
    public void shouldSuccessfullySaveUser() throws Exception {
        var query = new UserDaoQuery(CON);
        var user = UserDto.builder()
                .id(1L)
                .name("test_name")
                .nickName("test_nick")
                .createDate(LocalDateTime.now())
                .updateDate(null)
                .weight(70)
                .height(172)
                .language(UserDto.Language.RUS)
                .build();
        query.save(user);
        CON.commit();
        var found = query.findById(user.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get().getId(), user.getId());
        Assert.assertEquals(found.get().getName(), user.getName());
        Assert.assertEquals(found.get().getNickName(), user.getNickName());
        Assert.assertEquals(found.get().getCreateDate(), user.getCreateDate());
        Assert.assertNull(found.get().getUpdateDate());
        Assert.assertEquals(found.get().getWeight(), user.getWeight());
        Assert.assertEquals(found.get().getHeight(), user.getHeight());
        Assert.assertEquals(found.get().getLanguage(), user.getLanguage());
    }

    @Test
    public void shouldSuccessfullyUpdateUser() throws Exception {
        var query = new UserDaoQuery(CON);
        var user = UserDto.builder()
                .id(2L)
                .name("test_name2")
                .nickName("test_nick2")
                .createDate(LocalDateTime.now())
                .weight(70)
                .height(172)
                .language(UserDto.Language.ENG)
                .build();
        query.save(user);
        CON.commit();
        var forUpdate = UserDto.builder()
                .id(2L)
                .name("test_name_updated")
                .nickName("test_nick_updated")
                .updateDate(LocalDateTime.now())
                .weight(82)
                .height(172)
                .language(UserDto.Language.ENG)
                .build();
        query.update(forUpdate);
        CON.commit();
        var found = query.findById(forUpdate.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get().getId(), forUpdate.getId());
        Assert.assertEquals(found.get().getName(), forUpdate.getName());
        Assert.assertEquals(found.get().getNickName(), forUpdate.getNickName());
        Assert.assertEquals(found.get().getCreateDate(), user.getCreateDate());
        Assert.assertEquals(found.get().getUpdateDate(), forUpdate.getUpdateDate());
        Assert.assertEquals(found.get().getWeight(), forUpdate.getWeight());
        Assert.assertEquals(found.get().getHeight(), forUpdate.getHeight());
        Assert.assertEquals(found.get().getLanguage(), forUpdate.getLanguage());
    }
}