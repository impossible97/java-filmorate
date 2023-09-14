package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.entity.FilmEntity;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<FilmEntity, Integer> {

    @Query(value = "select f" +
            " from FilmToDirectorEntity ftd" +
            "  join FilmEntity f on f = ftd.film" +
            " where ftd.director.id = :id" +
            " order by f.likes.size desc")
    List<FilmEntity> getFilmsByDirectorAndLikes(@Param("id") int directorId);

    @Query(value = "select f" +
            " from FilmToDirectorEntity ftd" +
            "  join FilmEntity f on f = ftd.film" +
            " where ftd.director.id = :id" +
            " order by f.releaseDate")
    List<FilmEntity> getFilmsByDirectorAndYear(@Param("id") int directorId);
}
