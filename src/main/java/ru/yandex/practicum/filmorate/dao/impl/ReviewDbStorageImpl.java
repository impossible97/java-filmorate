package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ReviewDbStorageImpl implements ReviewDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Review makeReview(ResultSet rs, Integer rowNum) throws SQLException {
        int id = rs.getInt("id");
        String content = rs.getString("content");
        Boolean isPositive = rs.getBoolean("is_positive");
        int userId = rs.getInt("user_id");
        int filmId = rs.getInt("film_id");
        int useful = rs.getInt("useful");
        return new Review(id, content, isPositive, userId, filmId, useful);
    }

    @Override
    public List<Review> getFilmsReviews(int count) {
        String getFilmsReviews = "SELECT * " + "FROM reviews " + "ORDER BY useful DESC " + "LIMIT ?";
        return jdbcTemplate.query(getFilmsReviews, this::makeReview, count);
    }

    @Override
    public List<Review> getFilmsReviewsById(int filmId, int count) {
        String getFilmsReviews = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(getFilmsReviews, this::makeReview, filmId, count);
    }

    @Override
    public List<Long> getListId() {
        String getListId = "SELECT id FROM reviews";
        return jdbcTemplate.query(getListId, (rs, rowNum) -> rs.getLong("id"));
    }

    @Override
    public Review getReview(long id) {
        String getReview = "SELECT * FROM REVIEWS WHERE id = ?";
        return jdbcTemplate.queryForObject(getReview, this::makeReview, id);
    }

    @Override
    public void deleteReview(int id) {
        String deleteReview = "DELETE FROM REVIEWS WHERE id = ?";
        jdbcTemplate.update(deleteReview, id);
    }

    @Override
    public Review updateReview(Review review) {
        String updateReview = "UPDATE REVIEWS SET content = ?, is_positive = ? WHERE id = ?";
        jdbcTemplate.update(updateReview, review.getContent(), Boolean.toString(review.getIsPositive()), review.getReviewId());
        return getReview(review.getReviewId());
    }

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews").usingGeneratedKeyColumns("id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        return review;
    }

    @Override
    public void addLike(int reviewId, int userId) {
        String addLike = "MERGE INTO reviews_likes KEY(review_id, user_id) VALUES (?, ?, ?); "
                + "UPDATE reviews SET useful = IFNULL(useful, 0) + 1 WHERE id = ?;";
        jdbcTemplate.update(addLike, reviewId, userId, true, reviewId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String addDislike = "MERGE INTO reviews_likes KEY(review_id, user_id) VALUES (?, ?, ?); "
                + "UPDATE reviews SET useful = IFNULL(useful, 0) - 1 WHERE id = ?;";
        jdbcTemplate.update(addDislike, reviewId, userId, false, reviewId);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        String deleteLike = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?; " +
                "UPDATE reviews SET useful = IFNULL(useful, 0) - 1 WHERE id = ?;";
        jdbcTemplate.update(deleteLike, reviewId, userId);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        String deleteDislike = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?; " +
                "UPDATE reviews SET useful = IFNULL(useful, 0) + 1 WHERE id = ?;";
        jdbcTemplate.update(deleteDislike, reviewId, userId);
    }

}
