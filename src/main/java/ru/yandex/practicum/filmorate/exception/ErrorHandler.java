package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.warn("Validation error: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Ошибка валидации");
        error.put("message", e.getMessage());
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Ошибка валидации");
        StringBuilder message = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            if (message.length() > 0) {
                message.append("; ");
            }
            message.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage());
        });
        error.put("message", message.toString());
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.warn("Not found error: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Объект не найден");
        error.put("message", e.getMessage());
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("Data integrity violation: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Объект не найден");
        String message = e.getMessage();
        if (message != null && message.contains("FOREIGN KEY")) {
            if (message.contains("mpa_rating_id")) {
                error.put("message", "MPA рейтинг не найден");
            } else if (message.contains("genre_id")) {
                error.put("message", "Жанр не найден");
            } else {
                error.put("message", "Связанный объект не найден");
            }
        } else {
            error.put("message", "Нарушение целостности данных");
        }
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("Method argument type mismatch: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Ошибка валидации");
        error.put("message", "Неверный тип аргумента: " + e.getName());
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        log.error("Internal server error", e);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Внутренняя ошибка сервера");
        error.put("message", e.getMessage());
        return error;
    }
}

