package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.entity.DirectorEntity;
import ru.yandex.practicum.filmorate.dao.entity.FilmEntity;
import ru.yandex.practicum.filmorate.dao.entity.FilmToDirectorEntity;
import ru.yandex.practicum.filmorate.dao.repository.DirectorRepository;
import ru.yandex.practicum.filmorate.dao.repository.FilmRepository;
import ru.yandex.practicum.filmorate.dao.repository.FilmToDirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Setter
@Getter
public class DirectorService {

    @Autowired
    private DirectorMapper mapper;

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmToDirectorRepository filmToDirectorRepository;

    private boolean checkNameDirector(String name) {
        return directorRepository.findAll().stream().map(DirectorEntity::getName).noneMatch(name::equals);
    }

    public List<Director> getAllDirector() {
        return mapper.entityToDirectorList(directorRepository.findAll());
    }

    public Director getByIdDirector(Integer id) {
        return directorRepository.findById(id)
                .map(mapper::entityToDirector)
                .orElseThrow(() -> new NotFoundException("Такого режиссёра нет"));
    }

    public Director addDirector(Director director) {
        if (StringUtils.isBlank(director.getName())) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (!checkNameDirector(director.getName())) {
            throw new ValidationException("Такой режисссёр уже есть в списке");
        }
        DirectorEntity directorEntity = mapper.directorToDirectorEntity(director);
        return mapper.entityToDirector(directorRepository.save(directorEntity));
    }

    public Director updateDirector(Director director) {
        return directorRepository.findById(director.getId())
                .map(directorEntity -> {
                    directorEntity.setName(director.getName());
                    return directorEntity;
                })
                .map(directorRepository::save)
                .map(mapper::entityToDirector)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
    }

    public void deleteDirectorById(Integer id) {
        directorRepository.findById(id)
                .ifPresentOrElse(
                        directorRepository::delete,
                        () -> {
                            throw new NotFoundException("Режиссёр не найден");
                        });
    }

    @Transactional
    public void addDirectorsToFilm(Integer id, LinkedHashSet<Director> directors) {
        if (ObjectUtils.isEmpty(directors)) return;
        filmRepository.findById(id)
                .ifPresentOrElse(
                        film -> {
                            LinkedList<FilmToDirectorEntity> ftdEntities = directors.stream()
                                    .map(Director::getId)
                                    .map(directorRepository::findById)
                                    .map(optional -> optional.orElseThrow(() -> new NotFoundException("Режиссер не найден")))
                                    // TODO: при необходимости обработать ошибку ненайденного режиссера
                                    .map(directorEntity -> {
                                        FilmToDirectorEntity ftdEntity = new FilmToDirectorEntity();
                                        ftdEntity.setDirector(directorEntity);
                                        ftdEntity.setFilm(film);
                                        return ftdEntity;
                                    })
                                    .collect(Collectors.toCollection(LinkedList::new));
                            int count = 0;
                            for (FilmToDirectorEntity entity : ftdEntities) {
                                entity.setOrderBy(count++);
                                filmToDirectorRepository.save(entity);
                            }
                        },
                        () -> {
                            throw new RuntimeException("TODO");
                        }
                );
    }

    @Transactional
    public LinkedHashSet<Director> getAllDirectorsByFilmId(int filmId) {
        return filmToDirectorRepository.getByFilmIdOrderByOrderBy(filmId)
                .stream()
                .map(FilmToDirectorEntity::getDirector)
                .map(mapper::entityToDirector)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional
    public void deleteDirectorsByFilmId(Integer filmId) {
        filmRepository.findById(filmId)
                .map(FilmEntity::getId)
                .map(id -> {
                    log.info(String.format("Deleting directors ma[p to film id=%d", filmId));
                    return id;
                })
                .ifPresentOrElse(
                        filmToDirectorRepository::deleteByFilmId,
                        () -> {
                            throw new NotFoundException("Фильм не найден");
                        }
                );
    }

    public List<Film> getFilmsByDirectorOrderBy(int directorId, String sortByType) {
        directorRepository.findById(directorId)
                .orElseThrow(NotFoundException::new);
        return "likes".equals(sortByType)
                ? filmRepository.getFilmsByDirectorAndLikes(directorId).stream()
                .map(mapper::entityToFilm)
                .collect(Collectors.toList())
                : filmRepository.getFilmsByDirectorAndYear(directorId).stream()
                .map(mapper::entityToFilm)
                .collect(Collectors.toList());
    }
}
