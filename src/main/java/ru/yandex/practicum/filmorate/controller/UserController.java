package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Received request to create user: login={}, email={}", user.getLogin(), user.getEmail());
        validate(user);
        User createdUser = userStorage.createUser(user);
        log.debug("User created successfully: id={}, login={}", createdUser.getId(), createdUser.getLogin());
        return createdUser;
    }

    @GetMapping
    public List<User> getUsersList() {
        log.info("Received request to get all users");
        List<User> users = userStorage.getUsersList();
        log.debug("Returning {} users", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Received request to get user by id: {}", id);
        User user = userStorage.getUserById(id);
        log.debug("User found: id={}, login={}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Received request to update user: id={}, login={}", user.getId(), user.getLogin());
        validate(user);
        User updatedUser = userStorage.updateUser(user);
        log.debug("User updated successfully: id={}, login={}", updatedUser.getId(), updatedUser.getLogin());
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Received request to add friend: userId={}, friendId={}", id, friendId);
        userService.addFriend(id, friendId);
        log.debug("Friend added successfully: userId={}, friendId={}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Received request to remove friend: userId={}, friendId={}", id, friendId);
        userService.removeFriend(id, friendId);
        log.debug("Friend removed successfully: userId={}, friendId={}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Received request to get friends for user: id={}", id);
        List<User> friends = userService.getFriends(id);
        log.debug("Returning {} friends for user {}", friends.size(), id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Received request to get common friends: userId={}, otherId={}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.debug("Returning {} common friends for users {} and {}", commonFriends.size(), id, otherId);
        return commonFriends;
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелов");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}