package ru.yandex.practicum.filmorate.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.entity.DirectorEntity;

@Repository
public interface DirectorRepository extends JpaRepository<DirectorEntity, Integer> {
}
