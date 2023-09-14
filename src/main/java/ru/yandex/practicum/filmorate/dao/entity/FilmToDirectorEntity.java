package ru.yandex.practicum.filmorate.dao.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "films_to_directors")
public class FilmToDirectorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "film_id")
    private FilmEntity film;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "director_id")
    private DirectorEntity director;
    @Column(name = "order_by")
    private Integer orderBy;
}
