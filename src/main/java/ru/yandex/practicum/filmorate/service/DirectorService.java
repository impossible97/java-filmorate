package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@Service
@Setter
@Getter
public class DirectorService {

    @Autowired
    private DirectorDbStorage directorRepository;

    @Autowired
    private FilmDbStorage filmRepository;

    public List<Director> getAllDirector() {
        return directorRepository.getAll();
    }

    public Director getByIdDirector(Integer id) {
        return directorRepository.getDirectorById(id);
    }

    public Director addDirector(Director director) {
        if (StringUtils.isBlank(director.getName())) {
            throw new ValidationException("Имя не может быть пустым");
        }
        return directorRepository.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorRepository.updateDirector(director);
    }

    public void deleteDirectorById(Integer id) {
        directorRepository.deleteDirector(directorRepository.getDirectorById(id).getId());
    }

    public List<Film> getFilmsByDirectorOrderBy(int directorId, String sortByType) {
        directorRepository.getDirectorById(directorId);
        return directorRepository.getFilmsByDirectorSorted(directorId, sortByType);
    }
}
