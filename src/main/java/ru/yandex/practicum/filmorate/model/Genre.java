package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class Genre {

    private int id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
}
