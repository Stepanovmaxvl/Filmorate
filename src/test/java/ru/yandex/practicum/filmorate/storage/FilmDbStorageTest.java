package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, MpaRatingDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1, null));
        film.setGenres(new HashSet<>(Set.of(new Genre(1, null), new Genre(2, null))));

        Film created = filmStorage.createFilm(film);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Film");
        assertThat(created.getMpa()).isNotNull();
        assertThat(created.getMpa().getId()).isEqualTo(1);
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testFindFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1, null));
        Film created = filmStorage.createFilm(film);

        Optional<Film> filmOptional = filmStorage.findFilmById(created.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", created.getId())
                                .hasFieldOrPropertyWithValue("name", "Test Film")
                );
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1, null));
        Film created = filmStorage.createFilm(film);

        created.setName("Updated Film");
        Film updated = filmStorage.updateFilm(created);

        assertThat(updated.getName()).isEqualTo("Updated Film");
    }

    @Test
    @Sql(scripts = {"/schema.sql", "/data.sql"})
    public void testGetFilmsList() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new MpaRating(1, null));
        filmStorage.createFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        film2.setMpa(new MpaRating(2, null));
        filmStorage.createFilm(film2);

        List<Film> films = filmStorage.getFilmsList();

        assertThat(films).hasSizeGreaterThanOrEqualTo(2);
    }
}


