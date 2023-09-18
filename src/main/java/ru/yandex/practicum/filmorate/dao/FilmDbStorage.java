package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDbStorage {

    Film createFilm(Film film);

    Film updateFIlm(Film film);

    void deleteFilm(Integer filmId);

    List<Film> getAll();

    List<Film> findTopFilms(int limit);

    Film getFilmById(Integer id);

    List<Film> findRecommendedFilms(int id);

    List<Film> searchByTitle(String query);

    List<Film> searchByDirector(String query);

    List<Film> searchByTitleAndDirector(String query);
}
