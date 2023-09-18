package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> getAllDirector() {
        return directorService.getAllDirector();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable("id") Integer id) {
        return directorService.getByIdDirector(id);
    }

    @PostMapping("/directors")
    public Director addDirector(@RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirectorById(@PathVariable("id") Integer id) {
        directorService.deleteDirectorById(id);
    }


    @GetMapping("/films/director/{director-id}")
    public List<Film> getFilmsByDirectorOrderBy(
            @PathVariable("director-id") int directorId,
            @RequestParam("sortBy") String sortByType
    ) {
        if (StringUtils.isEmpty(sortByType) || !List.of("year", "likes").contains(sortByType)) {
            throw new InvalidParameterException("Invalid sort param");
        }
        return directorService.getFilmsByDirectorOrderBy(directorId, sortByType);
    }
}
