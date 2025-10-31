package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Пришёл запрос на создание пользователя с логином " + user.getLogin());
        validate(user);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Пришёл запрос на обновление данных пользователя с логином " + user.getLogin());
        validate(user);
        if (users.containsKey(user.getId())) {
            User newUser = users.get(user.getId());
            newUser.setEmail(user.getEmail());
            newUser.setLogin(user.getLogin());
            newUser.setName(user.getName());
            newUser.setBirthday(user.getBirthday());
            users.put(newUser.getId(), newUser);
            return newUser;
        } else {
            throw new ValidationException("Произошла ошибка");
        }
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