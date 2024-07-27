package com.example.demo.service;

import java.util.List;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dto.ReviewDto;
import com.example.demo.entity.Book;
import com.example.demo.entity.Review;
import com.example.demo.exception.OnlineBookStoreException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookService bookService;

    public ReviewService(ReviewRepository reviewRepository, BookService bookService) {
        this.reviewRepository = reviewRepository;
        this.bookService = bookService;
    }

    public ReviewDto submitReview(String isbn, ReviewDto reviewDto) {
        log.info("Entering submitReview()");
        Book book = bookService.findBookByIsbn(isbn);

        Review review = new Review();
        review.setBook(book);
        review.setReviewerName(reviewDto.getReviewerName());
        review.setContent(reviewDto.getContent());

        Review savedReview = reviewRepository.save(review);
        log.info("Leaving submitReview()");
        return convertToDto(savedReview);
    }

    @Cacheable(value = "review", key = "#isbn")
    public List<ReviewDto> fetchReviewsByIsbn(String isbn) {
        log.info("Entering fetchReviewsByIsbn()");
        Book book = bookService.findBookByIsbn(isbn);

        List<Review> reviews = reviewRepository.findByBook(book);
        log.info("Leaving fetchReviewsByIsbn()");
        return reviews.stream().map(this::convertToDto).toList();
    }

    @CachePut(value = "review", key = "#isbn")
    public ReviewDto modifyReview(String isbn, long reviewId, ReviewDto reviewDto) {
        log.info("Entering modifyReview()");
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new OnlineBookStoreException(String.format("Review not found for ID: %d", reviewId),
                        HttpStatus.NOT_FOUND));

        if (!review.getBook().getIsbn().equals(isbn)) {
            throw new OnlineBookStoreException("Review does not belong to the specified book", HttpStatus.CONFLICT);
        }

        review.setReviewerName(reviewDto.getReviewerName());
        review.setContent(reviewDto.getContent());

        Review updatedReview = reviewRepository.save(review);
        log.info("Leaving modifyReview()");
        return convertToDto(updatedReview);
    }

    private ReviewDto convertToDto(Review review) {
        log.info("Entering convertToDto()");
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setReviewerName(review.getReviewerName());
        reviewDto.setContent(review.getContent());
        reviewDto.setBookIsbn(review.getBook().getIsbn());
        log.info("Leaving convertToDto()");
        return reviewDto;
    }
}
