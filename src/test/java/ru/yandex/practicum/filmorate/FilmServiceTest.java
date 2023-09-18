package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.MPADbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorageImpl;
import ru.yandex.practicum.filmorate.dao.impl.MPADbStorageImpl;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:filmorate-test;MODE=PostgreSQL;"
        }
)
class FilmServiceTest {

    // TODO: переделать под Spring DI
    private static FilmService filmService = new FilmService(
            new FilmDbStorageImpl(new JdbcTemplate(), new MPADbStorageImpl(new JdbcTemplate()), new GenreDbStorageImpl(new JdbcTemplate()), null),
            new MPADbStorageImpl(new JdbcTemplate()),
            new GenreDbStorageImpl(new JdbcTemplate()),
            new JdbcTemplate());


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MPADbStorage mpaDbStorage;
    @Autowired
    private GenreDbStorage genreDbStorage;
    @Autowired
    private FilmDbStorageImpl filmDbStorage;
    @Autowired
    private FilmService myFilmService;

    private static final Integer FILM_ID = 1;

    @Test
    void validateFilmOk() {
        Film film = new Film();
        film.setId(FILM_ID);
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
    @Sql({"/data-test.sql"})
    void createFilmSucceed() {
        Film expected = new Film();
        expected.setId(FILM_ID);
        expected.setName("Name");
        expected.setDescription("Description");
        expected.setReleaseDate(LocalDate.of(2023, 1, 1));
        expected.setDuration(300);
        expected.setMpa(mpaDbStorage.findMpaById(1000));
        expected.setGenres(new LinkedHashSet<>(List.of(genreDbStorage.getAllGenres().get(0))));

        myFilmService.createFilm(expected);
        Film found = myFilmService.getFilm(FILM_ID);
        assertNotNull(found);
        assertEquals(expected.getName(), found.getName());
        assertEquals(expected.getDescription(), found.getDescription());
        assertEquals(expected.getDuration(), found.getDuration());
        assertEquals(expected.getReleaseDate(), found.getReleaseDate());
        assertEquals(1, found.getGenres().size());
        assertEquals(1000, found.getMpa().getId());
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
