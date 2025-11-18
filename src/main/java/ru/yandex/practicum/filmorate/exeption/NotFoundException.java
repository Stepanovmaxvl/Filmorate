package ru.yandex.practicum.filmorate.exeption;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }
}

