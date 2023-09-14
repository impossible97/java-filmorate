package ru.yandex.practicum.filmorate.dao.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "MPA_names")
public class MpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mpa_id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
}
