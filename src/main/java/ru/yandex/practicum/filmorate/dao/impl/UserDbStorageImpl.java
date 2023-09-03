package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;


@Component
@Slf4j
@AllArgsConstructor
public class UserDbStorageImpl implements UserDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        log.info("Получен GET-запрос");
        String sqlRow = "SELECT * FROM users";
        return jdbcTemplate.query(sqlRow, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            return user;
        });
    }

    @Override
    public User getUserById(Integer id) {
        log.info("Получен GET-запрос");
        String idQuery = "SELECT id FROM users WHERE id = ?";
        try {
            if (jdbcTemplate.queryForObject(idQuery, Integer.class, id) == null) {
                throw new NotFoundException("Пользователь с id " + id + " не найден в БД users");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден в БД users");
        }
        String query = "SELECT * FROM users WHERE id = ?";
        RowMapper<User> rowMapper = (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            return user;
        };
        return jdbcTemplate.queryForObject(query, rowMapper, id);
    }

    @Override
    public User createUser(User user) {
        log.info("Получен POST-запрос");
        String query = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = con -> {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        };
        jdbcTemplate.update(creator, keyHolder);
        Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Получен PUT-запрос");
        String idQuery = "SELECT id FROM users WHERE id = ?";
        int id = user.getId();

        try {
            if (jdbcTemplate.queryForObject(idQuery, Integer.class, id) == null) {
                throw new NotFoundException("Пользователь с id " + id + " не найден в БД users");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден в БД users");
        }
        String query = "UPDATE users SET email = ?, login = ?, name = ?, birthday= ? WHERE id = ?";
        jdbcTemplate.update(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {

        log.info("Получен DELETE-запрос");
        String query = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(query, userId);
    }
}
