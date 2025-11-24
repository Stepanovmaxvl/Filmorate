package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Received request to create film: name={}", film.getName());
        return filmService.createFilm(film);
    }

    @GetMapping
    public List<Film> getFilmsList() {
        log.info("Received request to get all films");
        return filmService.getFilmsList();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        log.info("Received request to get film by id: {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Received request to update film: id={}, name={}", film.getId(), film.getName());
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received request to add like: filmId={}, userId={}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received request to remove like: filmId={}, userId={}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        log.info("Received request to get popular films: count={}", count);
        return filmService.getPopularFilms(count);
    }
}