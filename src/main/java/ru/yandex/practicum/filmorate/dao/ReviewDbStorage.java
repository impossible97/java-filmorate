package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDbStorage {

    List<Review> getFilmsReviews(int count);

    Review getReview(int id);

    void deleteReview(int id);

    Review updateReview(Review review);

    Review addReview(Review review);

    void addLike(int id, int userId);

    void addDislike(int id, int userId);

    void deleteLike(int id, int userId);

    void deleteDislike(int id, int userId);

    List<Review> getFilmsReviewsById(int filmId, int count);

    List<Integer> getListId();
}
