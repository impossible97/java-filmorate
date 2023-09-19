package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MPADbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;


@Service
@Slf4j
@AllArgsConstructor
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final MPADbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final JdbcTemplate jdbcTemplate;
    private final EventService eventService;
    static LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    static int MIN_LENGTH = 200;

    public void addLike(int filmId, int userId) {
        log.info("Получен PUT-запрос");
        String query = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(query, filmId, userId);

        final Event event = Event.builder()
            .userId(userId)
            .entityId(filmId)
            .eventType(EventType.LIKE)
            .operation(Operation.ADD)
            .build();
        eventService.raiseEvent(event);
    }

    public void deleteLike(int filmId, int userId) {
        String idQuery = "SELECT user_id FROM likes WHERE user_id = ?";
        try {
            if (jdbcTemplate.queryForObject(idQuery, Integer.class, userId) == null) {
                throw new NotFoundException("Пользователь с id " + userId + " не найден в likes");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден в likes");
        }

        log.info("Получен DELETE-запрос");
        String query = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(query, filmId, userId);

        final Event event = Event.builder()
            .userId(userId)
            .entityId(filmId)
            .eventType(EventType.LIKE)
            .operation(Operation.REMOVE)
            .build();
        eventService.raiseEvent(event);
    }

    public List<Mpa> findAllMpa() {
        return mpaDbStorage.findAllMpa();
    }

    public Mpa findMpaById(int mpaId) {
        return mpaDbStorage.findMpaById(mpaId);
    }

    public Genre findGenreById(int genreId) {
        return genreDbStorage.findGenreById(genreId);
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public List<Film> findCommonFilms(int userId, int friendId) {
        return filmDbStorage.findCommonFilms(userId, friendId);
    }

    public List<Film> findFilmsByLikes(Integer count) {
        log.info("Выполняется вывод самых популярных фильмов");
        return filmDbStorage.findTopFilms(count);
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getAll();
    }

    public Film getFilm(int id) {
        return filmDbStorage.getFilmById(id);
    }

    public void validate(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > MIN_LENGTH) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Максимальная длина описания 200");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_DATE)) {
            log.error("Валидация не пройдена");
            throw new ValidationException(("Дата релиза не должна быть раньше 28 декабря 1895 года"));
        }
        if (film.getDuration() <= 0) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Продолжительность должна быть положительной");
        }
        log.info("Валидация пройдена");
    }

    public Film createFilm(Film film) {
        validate(film);
        return filmDbStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmDbStorage.updateFIlm(film);
    }

    public void deleteFilm(int id) {
        log.info("Выполняется операция удаления фильма");
        filmDbStorage.deleteFilm(id);
    }

    public List<Film> searchFilms(String query, String by) {
        log.info("Выполняется поиск фильмов");

        switch (by) {
            case "title":
                return filmDbStorage.searchByTitle(query);
            case "director":
                return filmDbStorage.searchByDirector(query);
            case "title,director":
                return filmDbStorage.searchByTitleAndDirector(query);
            default:
                return new ArrayList<>();
        }
    }

    public List<Film> findRecommendedFilms(int id) {
        return filmDbStorage.findRecommendedFilms(id);
    }
}