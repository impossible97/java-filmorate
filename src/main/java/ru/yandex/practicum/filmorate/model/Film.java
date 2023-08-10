package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private long id;
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
    private Set<Long> likes = new HashSet<>();
}
