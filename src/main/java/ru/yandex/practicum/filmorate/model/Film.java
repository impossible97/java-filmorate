package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    public Film(int id, String name, String description, LocalDate realiseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = realiseDate;
        this.duration = duration;
    }

    public Film() {

    }
}
