package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ReviewController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("POST /reviews");
        return reviewService.addReview(review);
    }

    @PutMapping("/reviews")
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("PUT /reviews");
        return reviewService.updateReview(review);
    }
    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable int id) {
        log.info("DELETE /reviews/" + id);
        reviewService.deleteReview(id);
    }

    @GetMapping("/reviews/{id}")
    public Review getReview(@PathVariable int id) {
        log.info("GET /reviews/" + id);
        return reviewService.getReview(id);
    }

    @GetMapping("/reviews")
    public List<Review> getFilmsReviews(@RequestParam(defaultValue = "0") final int filmId, @RequestParam(defaultValue = "10") final int count) {
        log.info("GET /reviews");
        return reviewService.getFilmsReviews(filmId, count);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /reviews/" + id + "/like/" + userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("PUT /reviews/" + id + "/dislike/" + userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE /reviews/" + id + "/like/" + userId);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("DELETE /reviews/" + id + "/dislike/" + userId);
        reviewService.deleteDisike(id, userId);
    }

}
