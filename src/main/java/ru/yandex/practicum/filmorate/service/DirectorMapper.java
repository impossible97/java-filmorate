package ru.yandex.practicum.filmorate.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dao.entity.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DirectorMapper {
    List<Director> entityToDirectorList(List<DirectorEntity> value);

    Director entityToDirector(DirectorEntity value);

    @Mapping(target = "id", ignore = true)
    DirectorEntity directorToDirectorEntity(Director value);

    /**
     * FILMS.
     */
    @Mapping(target = "directors", source = "ftd")
    Film entityToFilm(FilmEntity value);

    Mpa entityToMpa(MpaEntity value);

    Genre entityToGenre(GenreEntity value);

    LinkedHashSet<Genre> genreEntityListToGenreLinkedList(Set<GenreEntity> value);

    default LinkedHashSet<Director> directorEntityListToDirectorLinkedList(List<FilmToDirectorEntity> value) {
        return value.stream()
                .sorted(Comparator.comparing(FilmToDirectorEntity::getOrderBy).reversed())
                .map(FilmToDirectorEntity::getDirector)
                .map(this::entityToDirector)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
