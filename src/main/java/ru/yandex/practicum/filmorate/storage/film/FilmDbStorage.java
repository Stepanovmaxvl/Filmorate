package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Qualifier("filmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);

            Integer id = keyHolder.getKey().intValue();
            film.setId(id);

            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                saveGenres(id, film.getGenres());
            }

            log.info("Film created in DB: id={}, name={}", id, film.getName());
            return film;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating film", e);
            throw e;
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        deleteGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film.getId(), film.getGenres());
        }

        log.info("Film updated in DB: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        if (films.isEmpty()) {
            return films;
        }

        loadGenresForFilms(films);

        loadLikesForFilms(films);

        return films;
    }

    @Override
    public List<Film> getPopularFilms(int limit) {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name, COUNT(l.user_id) as likes_count " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, m.id, m.name " +
                "ORDER BY likes_count DESC, f.id ASC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, limit);

        if (films.isEmpty()) {
            return films;
        }


        loadGenresForFilms(films);

        loadLikesForFilms(films);

        return films;
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                "WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.get(0);

        loadGenresForFilms(List.of(film));
        loadLikesForFilms(List.of(film));

        return Optional.of(film);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        String description = rs.getString("description");
        film.setDescription(description);
        java.sql.Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        }
        film.setDuration(rs.getInt("duration"));

        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getInt("mpa_id"));
        mpaRating.setName(rs.getString("mpa_name"));
        film.setMpa(mpaRating);

        return film;
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        String placeholders = "?,".repeat(films.size());
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        String genresSql = "SELECT fg.film_id, g.id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON g.id = fg.genre_id " +
                "WHERE fg.film_id IN (" + placeholders + ") " +
                "ORDER BY fg.film_id, g.id";

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        java.util.Map<Integer, List<Genre>> genresMap = new java.util.HashMap<>();
        Object[] filmIdArray = filmIds.toArray();
        jdbcTemplate.query(genresSql, (rs, rowNum) -> {
            Integer filmId = rs.getInt("film_id");
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            genresMap.computeIfAbsent(filmId, k -> new java.util.ArrayList<>()).add(genre);
            return null;
        }, filmIdArray);

        for (Film film : films) {
            List<Genre> genres = genresMap.getOrDefault(film.getId(), new java.util.ArrayList<>());
            film.setGenres(new LinkedHashSet<>(genres));
        }
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        String placeholders = "?,".repeat(films.size());
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        String likesSql = "SELECT film_id, user_id FROM likes WHERE film_id IN (" + placeholders + ")";

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        java.util.Map<Integer, Set<Integer>> likesMap = new java.util.HashMap<>();
        Object[] filmIdArray = filmIds.toArray();
        jdbcTemplate.query(likesSql, (rs, rowNum) -> {
            Integer filmId = rs.getInt("film_id");
            Integer userId = rs.getInt("user_id");
            likesMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
            return null;
        }, filmIdArray);

        for (Film film : films) {
            Set<Integer> likes = likesMap.getOrDefault(film.getId(), new HashSet<>());
            film.setLikes(likes);
        }
    }

    private void saveGenres(Integer filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    private void deleteGenres(Integer filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sql = "MERGE INTO likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Like added: filmId={}, userId={}", filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Like removed: filmId={}, userId={}", filmId, userId);
    }
}

