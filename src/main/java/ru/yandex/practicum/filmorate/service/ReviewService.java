package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Objects;

@Service
public class ReviewService {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final ReviewDbStorage reviewDbStorage;

    @Autowired
    public ReviewService(ReviewDbStorageImpl reviewDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
    }

    public Review addReview(Review review) {
        checkReview(review);
        log.debug(review.toString());
        reviewDbStorage.addReview(review);
        return review;
    }

    public Review updateReview(Review review) {
        checkId(review.getReviewId());
        checkReview(review);
        return reviewDbStorage.updateReview(review);
    }

    public void deleteReview(int id) {
        checkId(id);
        reviewDbStorage.deleteReview(id);
    }

    public Review getReview(int id) {
        checkId(id);
        log.debug(Integer.toString(id));
        return reviewDbStorage.getReview(id);
    }

    public List<Review> getFilmsReviews(int filmId, int count) {
        if (filmId == 0) return reviewDbStorage.getFilmsReviews(count);
        checkFilmId(filmId);
        return reviewDbStorage.getFilmsReviewsById(filmId, count);
    }

    public void addLike(int id, int userId) {
        reviewDbStorage.addLike(id, userId);
    }

    public void addDislike(int id, int userId) {
        reviewDbStorage.addDislike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        reviewDbStorage.deleteLike(id, userId);
    }

    public void deleteDisike(int id, int userId) {
        reviewDbStorage.deleteDislike(id, userId);
    }

    private void checkReview(Review review) {
        if (review.getContent().isBlank()) {
            throw new ValidationException("Содержание отзыва не должно быть пустым");
        } else if (Objects.isNull(review.getIsPositive())) {
            throw new ValidationException("У отзыва должна быть оценка");
        } else if (Objects.isNull(review.getUserId())) {
            throw new ValidationException("Должен быть указан id пользователя");
        } else if (Objects.isNull(review.getFilmId())) {
            throw new ValidationException("Должен быть указан id фильма");
        }
        checkFilmId(review.getFilmId());
        checkUserId(review.getUserId());
    }

    private void checkUserId(Integer userId) {
        if (userId <= 0) {
            throw new NotFoundException("Неверный id пользователя");
        }
    }

    private void checkFilmId(int filmId) {
        if (filmId <= 0) {
            throw new NotFoundException("Неверный id фильма");
        }
    }

    private void checkId(long id) {
        if (!reviewDbStorage.getListId().contains(id)) {
            throw new NotFoundException("Неверный id отзыва");
        }
    }

}
