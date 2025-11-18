package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @Override
    public User createUser(User user) {
        user.setId(idCounter++);
        if (user.getFriends() == null) {
            user.setFriends(new java.util.HashSet<>());
        }
        users.put(user.getId(), user);
        log.info("User created: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        User existingUser = users.get(user.getId());
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());
        users.put(user.getId(), existingUser);
        log.info("User updated: id={}, login={}", user.getId(), user.getLogin());
        return existingUser;
    }

    @Override
    public List<User> getUsersList() {
        log.info("Getting users list: count={}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }
}

