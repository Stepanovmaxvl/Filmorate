package test.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

public class FilmControllerTest {
    private final FilmController filmController = new FilmController();
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void createFilm_noCreateFilm() {
        Film film = new Film();

        Assertions.assertThrows(RuntimeException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void createFilm_createFilm() {
        Film film = new Film();
        film.setDescription("Какой-то хороший фильм");
        film.setName("");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(106);

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void createFilm_noCreateFilm_descriptionLengthMore200() {
        Film film = new Film();
        film.setName("Всеведущий читатель");
        film.setDescription("Всеведущий читатель — боевик, фантастика, фэнтези, Южная Корея. молодой клерк Ким Док-ча читает веб-роман о конце света и герое, которому суждено вернуть мир в исходное состояние. Когда произведение заканчивается, герой становится единственным, кто его дочитал, и в разочаровании от концовки пишет автору письмо. После получения ответа с предложением переписать финал внезапно наступает конец света, а Ким Док-ча оказывается единственным, кто знает, что делать. ");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(106);
        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void createFilm_noCreateFilm_dateBefore() {
        Film film = new Film();
        film.setName("Какой-то хороший фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        film.setDuration(106);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void createFilm_noCreateFilm_dateAfter() {
        Film film = new Film();
        film.setName("Какой-то хороший фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1994, Month.DECEMBER, 16));
        film.setDuration(0);
        Assertions.assertFalse(validator.validate(film).isEmpty());
    }
}