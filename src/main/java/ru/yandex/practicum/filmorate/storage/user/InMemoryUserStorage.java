package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @Override
    public User createUser(User user) {
        user.setId(idCounter++);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        log.info("User created: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new IllegalStateException("Attempt to update non-existing user with id " + user.getId());
        }
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
    public void addFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user == null || friend == null) {
            return;
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user == null || friend == null) {
            return;
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        User user = users.get(userId);
        if (user == null) {
            return List.of();
        }
        return user.getFriends().stream()
                .map(users::get)
                .filter(friend -> friend != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        User user = users.get(userId);
        User other = users.get(otherUserId);
        if (user == null || other == null) {
            return List.of();
        }

        Set<Integer> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(other.getFriends());

        return commonIds.stream()
                .map(users::get)
                .filter(friend -> friend != null)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }
}

