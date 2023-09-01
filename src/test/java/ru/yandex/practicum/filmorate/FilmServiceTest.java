package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorageImpl;
import ru.yandex.practicum.filmorate.dao.impl.MPADbStorageImpl;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {

    static FilmService filmService = new FilmService(
            new FilmDbStorageImpl(new JdbcTemplate(), new MPADbStorageImpl(new JdbcTemplate()), new GenreDbStorageImpl(new JdbcTemplate())),
            new MPADbStorageImpl(new JdbcTemplate()),
            new GenreDbStorageImpl(new JdbcTemplate()),
            new JdbcTemplate());


    @Test
    void validateFilmOk() {
        Film film = new Film();
        film.setId(1);
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(300);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        filmService.validate(film);
    }

    @Test
    void validateFilmFail() {
        final Film film = new Film();
        film.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Название не может быть пустым", exception.getMessage());


        film.setName("Титаник");
        film.setDescription("В первом и последнем плавании шикарного «Титаника» встречаются двое." +
                " Пассажир нижней палубы Джек выиграл билет в карты, а богатая наследница Роза отправляется в Америку," +
                " чтобы выйти замуж по расчёту. Чувства молодых людей только успевают расцвести, и даже не классовые" +
                " различия создадут испытания влюблённым, а айсберг, вставший на пути считавшегося" +
                " непотопляемым лайнера.");
        exception = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Максимальная длина описания 200", exception.getMessage());


        film.setDescription("Фильм с ДиКаприо");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        exception = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Дата релиза не должна быть раньше 28 декабря 1895 года", exception.getMessage());


        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(-30);
        exception = assertThrows(ValidationException.class, () -> filmService.validate(film));
        assertEquals("Продолжительность должна быть положительной", exception.getMessage());
    }
}
