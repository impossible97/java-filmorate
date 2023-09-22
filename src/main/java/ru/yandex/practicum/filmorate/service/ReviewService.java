package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;

    private final EventService eventService;

    @Autowired
    public ReviewService(ReviewDbStorageImpl reviewDbStorage, EventService eventService) {
        this.reviewDbStorage = reviewDbStorage;
        this.eventService = eventService;
    }

    public Review addReview(Review review) {
        log.debug(Objects.toString(review));
        checkReview(review);
        final Review addedReview = reviewDbStorage.addReview(review);

        final Event event = Event.builder()
            .userId(addedReview.getUserId())
            .entityId(addedReview.getReviewId())
            .eventType(EventType.REVIEW)
            .operation(Operation.ADD)
            .build();
        eventService.raiseEvent(event);

        return addedReview;
    }

    public Review updateReview(Review review) {
        final Review storedReview = reviewDbStorage.getReview(review.getReviewId());
        if (storedReview == null) {
            throw new NotFoundException("Неверный id отзыва");
        }

        checkReview(review);
        final Review updatedReview = reviewDbStorage.updateReview(review);

        final Event event = Event.builder()
            .userId(storedReview.getUserId())
            .entityId(storedReview.getReviewId())
            .eventType(EventType.REVIEW)
            .operation(Operation.UPDATE)
            .build();
        eventService.raiseEvent(event);

        return updatedReview;
    }

    public void deleteReview(int id) {
        final Review storedReview = reviewDbStorage.getReview(id);
        if (storedReview == null) {
            throw new NotFoundException("Неверный id отзыва");
        }

        reviewDbStorage.deleteReview(id);

        final Event event = Event.builder()
            .userId(storedReview.getUserId())
            .entityId(storedReview.getReviewId())
            .eventType(EventType.REVIEW)
            .operation(Operation.REMOVE)
            .build();
        eventService.raiseEvent(event);
    }

    public Review getReview(int id) {
        checkId(id);
        log.debug(Integer.toString(id));
        return reviewDbStorage.getReview(id);
    }

    public List<Review> getFilmsReviews(int filmId, int count) {
        if (filmId == 0) {
            return reviewDbStorage.getFilmsReviews(count);
        } else {
            checkFilmIdPositivity(filmId);
            return reviewDbStorage.getFilmsReviewsById(filmId, count);
        }
    }

    public void addLike(int reviewId, int userId) {
        reviewDbStorage.addLike(reviewId, userId);
    }

    public void addDislike(int id, int userId) {
        reviewDbStorage.addDislike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        reviewDbStorage.deleteLike(id, userId);
    }

    public void deleteDislike(int id, int userId) {
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
        checkFilmIdPositivity(review.getFilmId());
        checkUserIdPositivity(review.getUserId());
    }

    private void checkUserIdPositivity(Integer userId) {
        if (userId <= 0) {
            throw new NotFoundException("Неверный id отзыва");
        }
    }

    private void checkFilmIdPositivity(int filmId) {
        if (filmId <= 0) {
            throw new NotFoundException("Неверный id отзыва");
        }
    }

    private void checkId(int id) {
        if (!reviewDbStorage.getListId().contains(id)) {
            throw new NotFoundException("Неверный id отзыва");
        }
    }

}
