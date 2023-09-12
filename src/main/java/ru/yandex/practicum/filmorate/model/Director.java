package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;

public class Director {

    private int id;
    @NotBlank
    private String name;
}
