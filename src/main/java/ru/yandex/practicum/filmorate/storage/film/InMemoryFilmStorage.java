package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Film existingFilm = films.get(film.getId());
        if (existingFilm == null) {
            throw new IllegalStateException("Attempt to update non-existing film with id " + film.getId());
        }
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
    public List<Film> getPopularFilms(int limit) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }
}

