package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        validateUser(user);
        normalizeName(user);
        User created = userStorage.createUser(user);
        log.info("User created: id={}, login={}", created.getId(), created.getLogin());
        return created;
    }

    public User updateUser(User user) {
        validateUser(user);
        getUserOrThrow(user.getId());
        normalizeName(user);
        User updated = userStorage.updateUser(user);
        log.info("User updated: id={}, login={}", updated.getId(), updated.getLogin());
        return updated;
    }

    public List<User> getUsersList() {
        return userStorage.getUsersList();
    }

    public User getUserById(Integer id) {
        return getUserOrThrow(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        
        userStorage.addFriend(user.getId(), friend.getId());
        log.info("User {} added friend {}", userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        
        userStorage.removeFriend(user.getId(), friend.getId());
        log.info("User {} removed friend {}", userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        getUserOrThrow(userId);
        List<User> friends = userStorage.getFriends(userId);
        log.info("Getting friends list for user {}: count={}", userId, friends.size());
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherId);
        List<User> commonFriends = userStorage.getCommonFriends(userId, otherId);
        log.info("Getting common friends for users {} and {}: count={}", userId, otherId, commonFriends.size());
        return commonFriends;
    }

    private User getUserOrThrow(Integer userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелов");
        }
    }

    private void normalizeName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}