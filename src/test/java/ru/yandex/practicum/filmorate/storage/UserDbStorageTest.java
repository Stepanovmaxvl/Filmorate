package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.createUser(user);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("test@example.com");
        assertThat(created.getLogin()).isEqualTo("testuser");
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testFindUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = userStorage.createUser(user);

        Optional<User> userOptional = userStorage.findUserById(created.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", created.getId())
                                .hasFieldOrPropertyWithValue("email", "test@example.com")
                );
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = userStorage.createUser(user);

        created.setName("Updated Name");
        User updated = userStorage.updateUser(created);

        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testGetUsersList() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.createUser(user2);

        List<User> users = userStorage.getUsersList();

        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testAddFriend() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User created1 = userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User created2 = userStorage.createUser(user2);

        userStorage.addFriend(created1.getId(), created2.getId());

        List<User> friends = userStorage.getFriends(created1.getId());
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(created2.getId());
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testRemoveFriend() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User created1 = userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User created2 = userStorage.createUser(user2);

        userStorage.addFriend(created1.getId(), created2.getId());
        userStorage.removeFriend(created1.getId(), created2.getId());

        List<User> friends = userStorage.getFriends(created1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testGetCommonFriends() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User created1 = userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User created2 = userStorage.createUser(user2);

        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3");
        user3.setName("User 3");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
        User created3 = userStorage.createUser(user3);

        userStorage.addFriend(created1.getId(), created3.getId());
        userStorage.addFriend(created2.getId(), created3.getId());

        List<User> commonFriends = userStorage.getCommonFriends(created1.getId(), created2.getId());
        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(created3.getId());
    }
}


