package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MPADbStorage {

    List<Mpa> findAllMpa();

    Mpa findMpaById(Integer mpaId);
}
