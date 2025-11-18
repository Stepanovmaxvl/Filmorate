package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    @Override
    public Film createFilm(Film film) {
        film.setId(idCounter++);
        if (film.getLikes() == null) {
            film.setLikes(new java.util.HashSet<>());
        }
        films.put(film.getId(), film);
        log.info("Film created: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        Film existingFilm = films.get(film.getId());
        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        films.put(film.getId(), existingFilm);
        log.info("Film updated: id={}, name={}", film.getId(), film.getName());
        return existingFilm;
    }

    @Override
    public List<Film> getFilmsList() {
        log.info("Getting films list: count={}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return films.get(id);
    }
}

