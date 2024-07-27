package com.example.demo.service;

import java.util.Optional;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dto.AuthorDto;
import com.example.demo.entity.Author;
import com.example.demo.exception.OnlineBookStoreException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Cacheable(value = "author", key = "#authorId")
    public AuthorDto findAuthorById(Long authorId) {
        log.info("Entering findAuthorById()");
        Author author = fetchAuthorById(authorId)
                .orElseThrow(() -> new OnlineBookStoreException(String.format("Author not found for ID: %d", authorId),
                        HttpStatus.NOT_FOUND));
        log.info("Leaving findAuthorById()");
        return convertToDto(author);
    }

    public Optional<Author> fetchAuthorById(Long authorId) {
        log.info("Entering fetchAuthorById()");
        Optional<Author> author = authorRepository.findById(authorId);
        log.info("Leaving fetchAuthorById()");
        return author;
    }

    private AuthorDto convertToDto(Author author) {
        log.info("Entering convertToDto()");
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(author.getId());
        authorDto.setName(author.getName());
        log.info("Leaving convertToDto()");
        return authorDto;
    }
}
