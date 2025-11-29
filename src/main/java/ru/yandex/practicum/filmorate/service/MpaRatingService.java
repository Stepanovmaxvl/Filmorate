package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(Integer id) {
        return mpaRatingStorage.getMpaRatingById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id " + id + " не найден"));
    }
}


