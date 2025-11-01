package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import java.time.LocalDate;

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
}