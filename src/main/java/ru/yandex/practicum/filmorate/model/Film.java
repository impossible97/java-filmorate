package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {

    @NotNull
    private int id;
    @NotBlank(message = "Поле name не должно быть пустым или содержать только пробельные символы")
    @NotNull
    private String name;
    @Size(max = 200, message = "Максимальная длинна описания 200 символов")
    @NotNull
    private String description;
    @NotNull(message = "releaseDate не может быть null")
    private LocalDate releaseDate;
    @Positive(message = "duration должно быть положительным")
    @NotNull
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
