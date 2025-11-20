package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        Film created = filmStorage.createFilm(film);
        log.info("Film created: id={}, name={}", created.getId(), created.getName());
        return created;
    }

    public Film updateFilm(Film film) {
        getFilmOrThrow(film.getId());
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
        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("User {} added like to film {}", userId, filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
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
}