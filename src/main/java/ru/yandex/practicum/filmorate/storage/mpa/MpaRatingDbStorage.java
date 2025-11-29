package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToMpaRating);
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(Integer id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        List<MpaRating> ratings = jdbcTemplate.query(sql, this::mapRowToMpaRating, id);
        return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings.get(0));
    }

    private MpaRating mapRowToMpaRating(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getInt("id"));
        mpaRating.setName(rs.getString("name"));
        return mpaRating;
    }
}


