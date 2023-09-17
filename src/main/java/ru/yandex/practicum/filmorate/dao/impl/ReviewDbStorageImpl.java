package ru.yandex.practicum.filmorate.dao.impl;

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

    public ReviewDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Review makeReview(ResultSet rs) throws SQLException {
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
        String getFilmsReviews = "SELECT * " +
                "FROM reviews " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(getFilmsReviews, (rs, rowNum) -> makeReview(rs), count);
    }

    @Override
    public List<Review> getFilmsReviewsById(int filmId, int count) {
        String getFilmsReviews = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(getFilmsReviews, (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public List<Long> getListId() {
        String getFilmsReviews = "SELECT id FROM reviews";
        return jdbcTemplate.query(getFilmsReviews, (rs, rowNum) -> rs.getLong("id"));
    }

    @Override
    public Review getReview(long id) {
        String getReview = "SELECT * FROM REVIEWS WHERE id = ?";
        return jdbcTemplate.queryForObject(getReview, (rs, rowNum) -> makeReview(rs), id);
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
        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews").usingGeneratedKeyColumns("id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        return review;
    }

    @Override
    public void addLike(int id, int userId) {
        String addLike =
                "INSERT INTO reviews_like (review_id, user_id_like) VALUES (?, ?);" +
                        "UPDATE REVIEWS SET useful = IFNULL(useful, 0) + 1 WHERE id = ?;";
        jdbcTemplate.update(addLike, id, userId, id);
        //deleteDislike(id, userId);
    }

    @Override
    public void addDislike(int id, int userId) {
        String addDislike =
                "INSERT INTO reviews_dislike (review_id, user_id_dislike) VALUES (?, ?);" +
                        "UPDATE REVIEWS SET useful = IFNULL(useful, 0) - 1 WHERE id = ?;";
        jdbcTemplate.update(addDislike, id, userId, id);
        //deleteLike(id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        String deleteLike = "DELETE FROM reviews_like WHERE review_id = ? AND user_id_like = ?";
        jdbcTemplate.update(deleteLike, id, userId);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        String deleteDislike = "DELETE FROM reviews_dislike WHERE review_id = ? AND user_id_dislike = ?";
        jdbcTemplate.update(deleteDislike, id, userId);
    }

}
