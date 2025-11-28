package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);
    
    Film updateFilm(Film film);
    
    List<Film> getFilmsList();

    List<Film> getPopularFilms(int limit);
    
    Optional<Film> findFilmById(Integer id);
    
    void addLike(Integer filmId, Integer userId);
    
    void removeLike(Integer filmId, Integer userId);
}
