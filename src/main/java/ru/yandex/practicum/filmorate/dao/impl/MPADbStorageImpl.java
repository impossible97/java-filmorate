package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MPADbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class MPADbStorageImpl implements MPADbStorage {

    JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAllMpa() {
        log.info("Получен GET-запрос");
        String query = "SELECT * FROM MPA_names";

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
    }

    @Override
    public Mpa findMpaById(Integer mpaId) {
        log.info("Получен GET-запрос");
        String query = "SELECT * FROM MPA_names WHERE mpa_id = ?";
        try {
            RowMapper<Mpa> rowMapper = (rs, rowNum) -> {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("name"));

                return mpa;
            };
            return jdbcTemplate.queryForObject(query, rowMapper, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с id " + mpaId + " не найден в БД MPA_names");
        }
    }

    @Override
    public void addMpa(int mpaId, Integer filmId) {
        String query = "INSERT INTO MPA (mpa_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(query, mpaId, filmId);
    }

    @Override
    public void deleteMpa(Integer filmId) {
        String idQuery = "SELECT film_id FROM MPA WHERE film_id = ?";
        try {
            if (jdbcTemplate.queryForObject(idQuery, Integer.class, filmId) == null) {
                throw new NotFoundException("Фильм с id " + filmId + " не найден в БД MPA");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден в БД MPA");
        }
        String query = "DELETE FROM MPA WHERE film_id = ?";
        jdbcTemplate.update(query, filmId);

    }
}
