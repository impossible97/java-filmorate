package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class GenreController {

    private final FilmService filmService;

    @GetMapping("/genres")
    public List<Genre> getAll() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable int id) {
        return filmService.findGenreById(id);
    }
}
