package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MPADbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
@Qualifier("DAO-film object")
@Slf4j
@AllArgsConstructor
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MPADbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final DirectorDbStorage directorDbStorage;

    private final FilmRowMapper filmRowMapper = new FilmRowMapper();


    @Override
    public List<Film> getAll() {
        log.info("Получен GET-запрос");
        String sqlRow = "SELECT * FROM films";
        return jdbcTemplate.query(sqlRow, (rs, rowNum) -> {
            Film film = new Film();
            int filmId = rs.getInt("id");
            film.setId(filmId);
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(mpaDbStorage.findMpaById(rs.getInt("rating_id")));
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
            film.setDirectors(directorDbStorage.getAllDirectorsByFilmId(filmId));
            return film;
        });
    }

    @Override
    public Film getFilmById(Integer id) {
        log.info("Получен GET-запрос");
        String idQuery = "SELECT id FROM films WHERE id = ?";

        try {
            if (jdbcTemplate.queryForObject(idQuery, Integer.class, id) == null) {
                throw new NotFoundException("Фильм с id " + id + " не найден в БД films");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id " + id + " не найден в БД films");
        }
        String query = "SELECT * FROM films WHERE id = ?";
        RowMapper<Film> rowMapper = (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(mpaDbStorage.findMpaById(rs.getInt("rating_id")));
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
            film.setDirectors(directorDbStorage.getAllDirectorsByFilmId(film.getId()));
            return film;
        };
        return jdbcTemplate.queryForObject(query, rowMapper, id);
    }

    @Override
    public List<Film> findCommonFilms(int userId, int friendId) {
        log.info("Получен GET-запрос");
        String sql =
                "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID," +
                        " COUNT(*) AS like_count" +
                        " FROM likes l" +
                        " JOIN films f ON f.id = l.film_id" +
                        " WHERE l.user_id IN (?, ?)" +
                        " GROUP BY f.id" +
                        " HAVING COUNT(DISTINCT l.user_id) = 2" +
                        " ORDER BY like_count DESC;";
        return jdbcTemplate.query(sql, new FilmRowMapper(), userId, friendId);
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Получен POST-запрос");
        String query = "INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = con -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        };
        jdbcTemplate.update(creator, keyHolder);
        Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(id);
        genreDbStorage.addGenre(film.getId(), film.getGenres());
        directorDbStorage.addDirectorsToFilm(film.getId(), film.getDirectors());

        return film;
    }

    @Override
    public Film updateFIlm(Film film) {
        log.info("Получен PUT-запрос");
        genreDbStorage.deleteGenre(film.getId());
        genreDbStorage.addGenre(film.getId(), film.getGenres());
        String idQuery = "SELECT id FROM films WHERE id = ?";
        int id = film.getId();

        try {
            if (jdbcTemplate.queryForObject(idQuery, Integer.class, id) == null) {
                throw new NotFoundException("Фильм с id " + id + " не найден в БД films");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id " + id + " не найден в БД films");
        }
        String query = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE id = ?";
        jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
        directorDbStorage.deleteDirectorsFromFilm(film.getId(), null);
        directorDbStorage.addDirectorsToFilm(film.getId(), film.getDirectors());
        return film;
    }

    @Override
    public void deleteFilm(Integer filmId) {
        String query = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(query, filmId);
    }

    @Override
    public List<Film> findTopFilms(int limit) {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, filmRowMapper, limit);

    }

    @Override
    public List<Film> findTopFilmsByGenre(int limit, int genreId) {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "JOIN film_genres fg on f.id = fg.film_id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmRowMapper(), genreId, limit);
    }

    @Override
    public List<Film> findTopFilmsByYear(int limit, int year) {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "WHERE EXTRACT(YEAR FROM f.releaseDate) = ? " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmRowMapper(), year, limit);
    }

    @Override
    public List<Film> findTopFilms(int limit, int genreId, int year) {
        String sql = "SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count " +
            "FROM films f " +
            "JOIN film_genres fg on f.id = fg.film_id " +
            "LEFT JOIN likes l ON f.id = l.film_id " +
            "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM releaseDate) = ? " +
            "GROUP BY f.id " +
            "ORDER BY likes_count DESC " +
            "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmRowMapper(), genreId, year, limit);
    }

    private class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(mpaDbStorage.findMpaById(rs.getInt("rating_id")));
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
            film.setDirectors(directorDbStorage.getAllDirectorsByFilmId(film.getId()));
            return film;
        }
    }

    @Override
    public List<Film> searchByTitle(String title) {
        String likeQuery = "%" + title + "%";
        String sql = String.format("SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE f.name ILIKE '%s' " +
                "GROUP BY f.id " +
                "ORDER BY likes_count DESC", likeQuery);
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    @Override
    public List<Film> searchByDirector(String director) {
        String likeQuery = "%" + director + "%";
        String sql = String.format("SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE f.id IN " +
                "(SELECT film_id FROM films_to_directors fd " +
                "JOIN director d ON fd.director_id = d.id " +
                "WHERE d.name ILIKE '%s') " +
                "GROUP BY f.id, f.name " +
                "ORDER BY likes_count DESC, f.name", likeQuery);
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    @Override
    public List<Film> searchByTitleAndDirector(String query) {
        String likeQuery = "%" + query + "%";
        String sql = String.format("SELECT f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "WHERE f.name ILIKE '%s' OR f.id IN " +
                "(SELECT film_id FROM films_to_directors fd " +
                "JOIN director d ON fd.director_id = d.id " +
                "WHERE d.name ILIKE '%s') " +
                "GROUP BY f.id, f.name " +
                "ORDER BY likes_count DESC, f.name", likeQuery, likeQuery);
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    @Override
    public List<Film> findRecommendedFilms(int id) {
        log.info("Получен GET-запрос вызвавший метод findRecommendedFilms");
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, f.RATING_ID" +
                " FROM likes l" +
                " JOIN films f ON f.id = l.film_id" +
                " WHERE l.user_id in (" +   //Ищем нужного пользователя
                " SELECT l2.user_id" +
                " FROM likes l1" +
                " JOIN likes l2 ON l1.film_id = l2.film_id" +
                " AND l1.user_id != l2.user_id" +
                " WHERE l1.user_id = ?" +
                " GROUP BY l1.user_id, l2.user_id" +
                " ORDER BY COUNT(*) DESC" +
                " LIMIT 1)" +
                " AND l.film_id NOT IN (" + // убираем фильмы лайкнутые искомым пользователем
                " SELECT film_id" +
                " FROM likes" +
                " WHERE user_id = ?" +
                " );";
        return jdbcTemplate.query(sql, filmRowMapper, id, id);
    }
}
