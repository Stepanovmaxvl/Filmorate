package test.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;

public class UserControllerTest {
    private UserStorage userStorage;
    private UserService userService;
    private UserController userController;
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @BeforeEach
    public void init() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    public void createUser_noCreateUser() {
        User user = new User();

        Assertions.assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    public void createUser_noCreateUser_emptyName() {
        User user = new User();
        user.setEmail("Test@Gmail.com");
        user.setLogin("Test");
        user.setName("");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        User user1 = userController.createUser(user);
        Assertions.assertEquals(user1.getName(), user1.getLogin());
    }

    @Test
    public void createUser_noCreateUser_emptyLogin() {
        User user = new User();
        user.setEmail("Test@Gmail.com");
        user.setLogin("");
        user.setName("Test");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user).isEmpty());


        User user1 = new User();

        user.setEmail("Test@Gmail.com");
        user.setLogin("Test");
        user.setName("Test");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user1).isEmpty());

    }

    @Test
    public void createUser_noCreateUser_emptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("Test");
        user.setName("Test");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user).isEmpty());


        User user1 = new User();
        user.setEmail("Test@gmail.com");
        user.setLogin("Test");
        user.setName("Test");
        user.setBirthday(LocalDate.of(2003, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user1).isEmpty());

    }

    @Test
    public void createUser_noCreateUser_futureYearBirthday() {
        User user = new User();
        user.setEmail("Test@Gmail.com");
        user.setLogin("Test");
        user.setName("Test");
        user.setBirthday(LocalDate.of(3000, Month.NOVEMBER, 24));
        Assertions.assertFalse(validator.validate(user).isEmpty());
    }
}