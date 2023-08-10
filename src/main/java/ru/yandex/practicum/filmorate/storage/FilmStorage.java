package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAll();

    Film create(Film film);

    Film updateFIlm(Film film);

    void deleteFilm(long id);

    Film getFilmById(long id);
}
