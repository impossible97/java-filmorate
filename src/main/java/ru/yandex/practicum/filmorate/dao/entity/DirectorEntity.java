package ru.yandex.practicum.filmorate.dao.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "director")
public class DirectorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
}