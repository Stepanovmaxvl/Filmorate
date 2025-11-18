package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        
        film.getLikes().add(userId);
        log.info("User {} added like to film {}", userId, filmId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        
        film.getLikes().remove(userId);
        log.info("User {} removed like from film {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;
        
        List<Film> popularFilms = filmStorage.getFilmsList().stream()
                .sorted(Comparator.comparing((Film f) -> f.getLikes().size()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        
        log.info("Getting {} popular films", popularFilms.size());
        return popularFilms;
    }
}

