package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "название фильма не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание должно быть короче двухсот символов!")
    private String description;
    @NotNull
    @FilmReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new LinkedHashSet<>();
    @NotNull(message = "MPA рейтинг не может быть null")
    private MpaRating mpa;
}