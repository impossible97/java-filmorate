package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;

public interface GenreDbStorage {

    void addGenre(Integer filmId, LinkedHashSet<Genre> genres);

    void deleteGenre(Integer filmId);

    LinkedHashSet<Genre> getGenresByFilmId(Integer filmId);

    Genre findGenreById(Integer mpaId);

    List<Genre> getAllGenres();
}
