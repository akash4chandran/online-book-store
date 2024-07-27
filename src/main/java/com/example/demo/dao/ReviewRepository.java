package com.example.demo.dao;

import java.util.List;

import com.example.demo.entity.Book;
import com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(Book book);
}
