package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDbStorage {

    Film createFilm(Film film);

    Film updateFIlm(Film film);

    void deleteFilm(Integer filmId);

    List<Film> getAll();

    List<Film> findTopFilms(int limit);

    List<Film> findTopFilmsByGenre(int limit, int genreId);

    List<Film> findTopFilmsByYear(int limit, int year);

    List<Film> findTopFilms(int limit, int genreId, int year);

    Film getFilmById(Integer id);


    List<Film> findCommonFilms(int userId, int friendId);

    List<Film> findRecommendedFilms(int id);

    List<Film> searchByTitle(String query);

    List<Film> searchByDirector(String query);

    List<Film> searchByTitleAndDirector(String query);
}
