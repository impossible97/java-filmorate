package ru.yandex.practicum.filmorate.dao.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "genre_name")
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
}
