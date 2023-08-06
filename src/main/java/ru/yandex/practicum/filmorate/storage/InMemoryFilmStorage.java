package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer,Film> films = new HashMap<>();
    protected int generatedId = 0;


    @Override
    public List<Film> getAll() {
        log.info("Получен GET-запрос");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        log.info("Получен POST-запрос");
        final int id = ++generatedId;
        film.setId(id);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFIlm(Film film) {
        log.info("Получен PUT-запрос");
        if (!films.containsKey(film.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        log.info("Получен DELETE-запрос");
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }
}
