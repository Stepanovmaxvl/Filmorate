package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Received request to create film: name={}", film.getName());
        Film createdFilm = filmStorage.createFilm(film);
        log.debug("Film created successfully: id={}, name={}", createdFilm.getId(), createdFilm.getName());
        return createdFilm;
    }

    @GetMapping
    public List<Film> getFilmsList() {
        log.info("Received request to get all films");
        List<Film> films = filmStorage.getFilmsList();
        log.debug("Returning {} films", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Received request to get film by id: {}", id);
        Film film = filmStorage.getFilmById(id);
        log.debug("Film found: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Received request to update film: id={}, name={}", film.getId(), film.getName());
        Film updatedFilm = filmStorage.updateFilm(film);
        log.debug("Film updated successfully: id={}, name={}", updatedFilm.getId(), updatedFilm.getName());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received request to add like: filmId={}, userId={}", id, userId);
        filmService.addLike(id, userId);
        log.debug("Like added successfully: filmId={}, userId={}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received request to remove like: filmId={}, userId={}", id, userId);
        filmService.removeLike(id, userId);
        log.debug("Like removed successfully: filmId={}, userId={}", id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        log.info("Received request to get popular films: count={}", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.debug("Returning {} popular films", popularFilms.size());
        return popularFilms;
    }
}