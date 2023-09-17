package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MPADbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
/*Хранилище режиссеров. Самописная реализация JpaRepository по настоянию лида.
 * Класс содержит несамодокументированный код с антипаттернами, почти полностью скопирован с прошедшего ревью
 * в прошлом спринте класса-хранилища фильмов.
 * Множество тудушек обусловлены нечистым кодом и потенциально опасными местами (вопросы стека, NPE и прочее). */
public class DirectorDbStorageImpl extends BasePseudoRepository implements DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MPADbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Override
    public List<Director> getAll() {
        log.info("Получен GET-запрос");
        // TODO: фактически маппер (лямбда) дублируется в коде, это антипаттерн.
        // По настоянию лида взята его реализация (FilmDbStorageImpl
        // содержит внутренний класс маппера помимо дублирующихся лямбд).
        return jdbcTemplate.query(getSql("directors-get-all"), (rs, rowNum) -> {
            Director director = new Director();
            int directorId = rs.getInt("id");
            director.setId(directorId);
            director.setName(rs.getString("name"));
            return director;
        });
    }

    @Override
    public Director getDirectorById(Integer id) {
        log.info("Получен GET-запрос");
        // TODO: 1 лучше не генерировать эксепшен а делать типа select count(id) и сверять результат с нулем
        try {
            if (jdbcTemplate.query(getSql("directors-get-by-id"), (rs, cnt) -> "", id).isEmpty()) {
                throw new NotFoundException("Режиссер с id " + id + " не найден в БД director");
            }
        } catch (EmptyResultDataAccessException e) {
            // TODO: неправильно менять один стандартный эксепшен на другой, пересобирается стек, ресурсозатратная операция.
            //  Такое используется при наличии самописной иерархии exception. Но по настоянию лида используем как у него.
            throw new NotFoundException("Режиссер с id " + id + " не найден в БД director");
        }
        RowMapper<Director> rowMapper = (rs, rowNum) -> {
            // TODO: в хорошем коде тут должен быть билдер. По настоянию лида реализация взята из его кода.
            Director director = new Director();
            director.setId(rs.getInt("id"));
            director.setName(rs.getString("name"));
            return director;
        };
        return jdbcTemplate.queryForObject(getSql("directors-get-by-id"), rowMapper, id);
    }

    @Override
    public Director createDirector(Director director) {
        // TODO: какое отношение база (storage) имеет к post запросу? Что такое post-запрос для базы?
        // Логирование взято из реализации лида по его настоянию (такой стиль).
        log.info("Получен POST-запрос");

        // Новое. Проверка уникальности имени режиссера до вставки в базу, чтобы не провоцировать ошибку неуникального имени.
        checkUniqueNameByDirector(director);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = con -> {
            PreparedStatement statement = con.prepareStatement(
                    getSql("directors-add-without-id"), Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, director.getName());
            return statement;
        };
        jdbcTemplate.update(creator, keyHolder);
        // TODO: первичный ключ в РСУБД non null, более того, в данном случае на полях id есть причина использовать генераторы.
        // проверка на null избыточна. По настоянию лида взята его реализация.
        Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        director.setId(id);

        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        // TODO: какое отношение база (storage) имеет к PUT запросу? Что такое PUT-запрос для базы?
        // Логирование взято из реализации лида по его настоянию (такой стиль).
        log.info("Получен PUT-запрос");
        int id = director.getId();

        try {
            if (jdbcTemplate.query(getSql("directors-get-by-id"), (rs, cnt) -> "", id).isEmpty()) {
                throw new NotFoundException("Режиссер с id " + id + " не найден в БД director");
            }
        } catch (EmptyResultDataAccessException e) {
            // TODO: бесмысленный перехват одного коробочного исключения и генерация другого.
            //  Если так делать, то используют самописный эксепшен.
            //  По настоянию лида берем его реализацию.
            throw new NotFoundException("Режиссер с id " + id + " не найден в БД director");
        }
        // Новое: имя режиссера уникально, тут проверить это можно.
        // При обновлении фильма проверки на уникальность  нет, можно обновить фильм так,
        // что имя будет неуникально и словится ошибка БД.
        // Для режиссеров проверим уникальность сами, чтобы не заставлять СУБД генерировать ошибку.
        checkUniqueNameByDirector(director);
        jdbcTemplate.update(getSql("directors-update-by-id"),
                director.getName(),
                director.getId());
        return director;
    }

    private void checkUniqueNameByDirector(Director director) {
        Integer duplicatCnt = jdbcTemplate.queryForObject(
                getSql("directors-check-unique-by-name"), Integer.class,
                director.getName(), director.getId());
        if (!duplicatCnt.equals(0)) {
            throw new IllegalArgumentException(
                    String.format("Режиссер с таким именем уже есть (%s)!", director.getName()));
        }
    }

    @Override
    public void deleteDirector(Integer directorId) {
        // TODO: какое отношение база (storage) имеет к DELETE запросу?
        //  Что такое DELETE-запрос для базы? Удаление всей БД?
        // Логирование взято из реализации лида по его настоянию (такой стиль).
        log.info("Получен DELETE-запрос");
        jdbcTemplate.update(getSql("directors-delete-by-id"), directorId);
    }

    @Override
    public void addDirectorToFilm(Integer filmId, Integer directorId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = con -> {
            PreparedStatement statement = con.prepareStatement(
                    getSql("films-add-director"), Statement.RETURN_GENERATED_KEYS);
            int idx = 0;
            statement.setInt(++idx, directorId);
            statement.setInt(++idx, filmId);
            statement.setInt(++idx, filmId);
            return statement;
        };
        jdbcTemplate.update(creator, keyHolder);
    }

    public void addDirectorsToFilm(Integer id, LinkedHashSet<Director> directors) {
        if (ObjectUtils.isEmpty(directors)) return;
        directors.stream()
                .map(Director::getId)
                .map(this::getDirectorById)
                .map(Director::getId)
                .forEach(directorId -> this.addDirectorToFilm(id, directorId));
    }

    @Override
    public void deleteDirectorsFromFilm(Integer filmId, Integer directorId) {
        jdbcTemplate.update(getSql("films-delete-director"), filmId, directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSorted(int directorId, String sortByType) {
        String request = "likes".equals(sortByType) ? "films-by-director-by-likes" : "films-by-director-by-year";
        RowMapper<Film> rowMapper = (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(mpaDbStorage.findMpaById(rs.getInt("rating_id")));
            film.setGenres(genreDbStorage.getGenresByFilmId(film.getId()));
            film.setDirectors(getAllDirectorsByFilmId(film.getId()));
            return film;
        };
        return jdbcTemplate.query(getSql(request), rowMapper, directorId);
    }

    @Override
    public LinkedHashSet<Director> getAllDirectorsByFilmId(int filmId) {
        RowMapper<Director> rowMapper = (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("id"));
            director.setName(rs.getString("name"));
            return director;
        };
        return jdbcTemplate.queryForStream(
                getSql("films-get-directors-ordered"),
                rowMapper,
                filmId
        ).collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
