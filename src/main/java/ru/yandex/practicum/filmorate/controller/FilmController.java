package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    @Valid
    private final HashMap<Integer,Film> films = new HashMap<>();
    protected int generatedId = 0;

    @GetMapping("/films")
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Valid
    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        log.info("Получен POST-запрос");
        validate(film);
        final int id = ++generatedId;
        film.setId(id);
        films.put(film.getId(), film);
        return film;
    }

    @Valid
    @PutMapping("/films")
    public Film updateFIlm(@RequestBody Film film) {
        log.info("Получен PUT-запрос");
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Такого id не существует");
        }
        films.put(film.getId(), film);
        return film;
    }

    public void validate(Film film) {
        if (film.getName().isEmpty() || film.getName() == null) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200 || film.getDescription() == null) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Максимальная длина описания 200");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) || film.getReleaseDate() == null) {
            log.error("Валидация не пройдена");
            throw new ValidationException(("Дата релиза не должна быть раньше 28 декабря 1895 года"));
        }
        if (film.getDuration() < 0) {
            log.error("Валидация не пройдена");
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}
