package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmLikesComparator implements Comparator<Film> {

    @Override
    public int compare(Film film1, Film film2) {
        return Integer.compare(film2.getLikes(), film1.getLikes());
    }
}
