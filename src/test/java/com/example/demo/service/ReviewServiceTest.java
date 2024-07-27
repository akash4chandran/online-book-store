package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dto.ReviewDto;
import com.example.demo.entity.Book;
import com.example.demo.entity.Review;
import com.example.demo.exception.OnlineBookStoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;


@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReviewService reviewService;

    private Book book;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");

        review = new Review();
        review.setId(1);
        review.setBook(book);
        review.setReviewerName("John Doe");
        review.setContent("Great book!");

        reviewDto = new ReviewDto();
        reviewDto.setReviewerName("John Doe");
        reviewDto.setContent("Great book!");
    }

    @Test
    void submitReview_Success() {
        when(bookService.findBookByIsbn(anyString())).thenReturn(book);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDto result = reviewService.submitReview("1234567890", reviewDto);

        assertNotNull(result);
        assertEquals(review.getReviewerName(), result.getReviewerName());
        assertEquals(review.getContent(), result.getContent());
        verify(bookService, times(1)).findBookByIsbn(anyString());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void fetchReviewsByIsbn_Success() {
        when(bookService.findBookByIsbn(anyString())).thenReturn(book);
        when(reviewRepository.findByBook(any(Book.class))).thenReturn(List.of(review));

        List<ReviewDto> result = reviewService.fetchReviewsByIsbn("1234567890");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(review.getReviewerName(), result.get(0).getReviewerName());
        assertEquals(review.getContent(), result.get(0).getContent());
        verify(bookService, times(1)).findBookByIsbn(anyString());
        verify(reviewRepository, times(1)).findByBook(any(Book.class));
    }

    @Test
    void modifyReview_Success() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewDto updatedReviewDto = new ReviewDto();
        updatedReviewDto.setReviewerName("Jane Doe");
        updatedReviewDto.setContent("Updated review content");

        ReviewDto result = reviewService.modifyReview("1234567890", 1L, updatedReviewDto);

        assertNotNull(result);
        assertEquals(updatedReviewDto.getReviewerName(), result.getReviewerName());
        assertEquals(updatedReviewDto.getContent(), result.getContent());
        verify(reviewRepository, times(1)).findById(anyLong());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void modifyReview_ReviewNotFound() {
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            reviewService.modifyReview("1234567890", 1L, reviewDto);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(reviewRepository, times(1)).findById(anyLong());
    }

    @Test
    void modifyReview_ReviewDoesNotBelongToBook() {
        review.getBook().setIsbn("0987654321");
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            reviewService.modifyReview("1234567890", 1L, reviewDto);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(reviewRepository, times(1)).findById(anyLong());
    }
}