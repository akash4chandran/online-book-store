package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.dao.AuthorRepository;
import com.example.demo.dto.AuthorDto;
import com.example.demo.entity.Author;
import com.example.demo.exception.OnlineBookStoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorDto authorDto;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1);
        author.setName("Author Name");

        authorDto = new AuthorDto();
        authorDto.setId(1);
        authorDto.setName("Author Name");
    }

    @Test
    void findAuthorById_Success() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));

        AuthorDto result = authorService.findAuthorById(1L);

        assertNotNull(result);
        assertEquals(author.getId(), result.getId());
        assertEquals(author.getName(), result.getName());
        verify(authorRepository, times(1)).findById(anyLong());
    }

    @Test
    void findAuthorById_AuthorNotFound() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            authorService.findAuthorById(1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(authorRepository, times(1)).findById(anyLong());
    }

    @Test
    void fetchAuthorById_Success() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(author));

        Optional<Author> result = authorService.fetchAuthorById(1L);

        assertTrue(result.isPresent());
        assertEquals(author.getId(), result.get().getId());
        verify(authorRepository, times(1)).findById(anyLong());
    }

    @Test
    void fetchAuthorById_AuthorNotFound() {
        when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Author> result = authorService.fetchAuthorById(1L);

        assertFalse(result.isPresent());
        verify(authorRepository, times(1)).findById(anyLong());
    }

}