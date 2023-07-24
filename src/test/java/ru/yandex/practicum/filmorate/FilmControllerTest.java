package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    static FilmController filmController = new FilmController();

    @Test
    void validateFilmOk () {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setRealiseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(Duration.ofMinutes(30));
        filmController.validate(film);
    }

    @Test
    void validateFilmFail() {
        final Film film = new Film();
        film.setName("");
        exception.ValidationException exception = assertThrows(exception.ValidationException.class, () -> filmController.validate(film));
        assertEquals("Название не может быть пустым", exception.getMessage());


        film.setName("Титаник");
        film.setDescription("В первом и последнем плавании шикарного «Титаника» встречаются двое." +
                " Пассажир нижней палубы Джек выиграл билет в карты, а богатая наследница Роза отправляется в Америку," +
                " чтобы выйти замуж по расчёту. Чувства молодых людей только успевают расцвести, и даже не классовые" +
                " различия создадут испытания влюблённым, а айсберг, вставший на пути считавшегося" +
                " непотопляемым лайнера.");
        exception = assertThrows(exception.ValidationException.class, () -> filmController.validate(film));
        assertEquals("Максимальная длина описания 200", exception.getMessage());


        film.setDescription("Фильм с ДиКаприо");
        film.setRealiseDate(LocalDate.of(1800, 1, 1));
        exception = assertThrows(exception.ValidationException.class, () -> filmController.validate(film));
        assertEquals("Дата релиза не должна быть раньше 28 декабря 1895 года", exception.getMessage());


        film.setRealiseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ofMinutes(-30));
        exception = assertThrows(exception.ValidationException.class, () -> filmController.validate(film));
        assertEquals("Продолжительность должна быть положительной", exception.getMessage());
    }
}
