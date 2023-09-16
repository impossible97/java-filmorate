package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class GenreDbStorageImpl implements GenreDbStorage {

    JdbcTemplate jdbcTemplate;

    @Override
    public void addGenre(Integer filmId, LinkedHashSet<Genre> genres) {
        String query = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        genres.forEach(genre -> jdbcTemplate.update(query, filmId, genre.getId())
        );
    }

    @Override
    public void deleteGenre(Integer filmId) {
        String query = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(query, filmId);
    }

    @Override
    public LinkedHashSet<Genre> getGenresByFilmId(Integer filmId) {
        String query = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        List<Integer> genreIds = jdbcTemplate.queryForList(query, Integer.class, filmId);

        return genreIds.stream()
                .map(this::findGenreById)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Genre findGenreById(Integer genreId) {
        log.info("Получен GET-запрос");
        String query = "SELECT * FROM genre_name WHERE genre_id = ?";
        try {
            RowMapper<Genre> rowMapper = (rs, rowNum) -> {
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("name"));

                return genre;
            };
            return jdbcTemplate.queryForObject(query, rowMapper, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id " + genreId + " не найден в БД genre_name");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Получен GET-запрос");
        String query = "SELECT * FROM genre_name";

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        });
    }
}
