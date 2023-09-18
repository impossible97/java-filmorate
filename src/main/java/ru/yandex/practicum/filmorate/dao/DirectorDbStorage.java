package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.List;

public interface DirectorDbStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer directorId);

    List<Director> getAll();

    Director getDirectorById(Integer id);

    void addDirectorToFilm(Integer filmId, Integer directorId);

    void addDirectorsToFilm(Integer id, LinkedHashSet<Director> directors);

    void deleteDirectorsFromFilm(Integer filmId, Integer directorId);

    List<Film> getFilmsByDirectorSorted(int directorId, String sortByType);

    LinkedHashSet<Director> getAllDirectorsByFilmId(int filmId);
}