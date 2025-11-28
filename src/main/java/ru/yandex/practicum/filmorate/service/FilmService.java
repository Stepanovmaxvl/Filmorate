package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaRatingStorage mpaRatingStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage,
                       MpaRatingStorage mpaRatingStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public Film createFilm(Film film) {
        normalizeFilm(film);
        validateFilm(film);
        Film created = filmStorage.createFilm(film);
        log.info("Film created: id={}, name={}", created.getId(), created.getName());
        return created;
    }

    public Film updateFilm(Film film) {
        getFilmOrThrow(film.getId());
        normalizeFilm(film);
        validateFilm(film);
        Film updated = filmStorage.updateFilm(film);
        log.info("Film updated: id={}, name={}", updated.getId(), updated.getName());
        return updated;
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film getFilmById(Integer id) {
        return getFilmOrThrow(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.addLike(filmId, userId);
        log.info("User {} added like to film {}", userId, filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;
        return filmStorage.getPopularFilms(limit);
    }

    private Film getFilmOrThrow(Integer filmId) {
        return filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));
    }

    private void getUserOrThrow(Integer userId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void validateFilm(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new NotFoundException("MPA рейтинг не указан");
        }
        MpaRating mpa = mpaRatingStorage.getMpaRatingById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA рейтинг с id " + film.getMpa().getId() + " не найден"));
        film.setMpa(mpa);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            LinkedHashSet<Genre> resolvedGenres = film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .filter(genre -> genre.getId() != null)
                    .map(genre -> genreStorage.getGenreById(genre.getId())
                            .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден")))
                    .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
            film.setGenres(resolvedGenres);
        }
    }

    private void normalizeFilm(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new LinkedHashSet<>());
            return;
        }

        LinkedHashMap<Integer, Genre> uniqueOrderedGenres = new LinkedHashMap<>();
        film.getGenres().stream()
                .filter(Objects::nonNull)
                .filter(genre -> genre.getId() != null)
                .sorted(Comparator.comparing(Genre::getId))
                .forEach(genre -> uniqueOrderedGenres.put(genre.getId(), genre));
        film.setGenres(new LinkedHashSet<>(uniqueOrderedGenres.values()));
    }
}