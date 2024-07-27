package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.demo.dao.BookRepository;
import com.example.demo.dto.BookDto;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.exception.OnlineBookStoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.querydsl.core.BooleanBuilder;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private BookService bookService;

    private BookDto bookDto;
    private Book book;
    private Author author;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto();
        bookDto.setIsbn("1234567890");
        bookDto.setTitle("Test Book");
        bookDto.setAuthorId(1L);

        author = new Author();
        author.setId(1);
        author.setName("Author Name");

        book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setAuthor(author);
    }

    @Test
    void addNewBook_Success() {
        when(bookRepository.existsById(anyString())).thenReturn(false);
        when(authorService.fetchAuthorById(anyLong())).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(book);

        BookDto result = bookService.addNewBook(bookDto);

        assertNotNull(result);
        assertEquals(bookDto.getIsbn(), result.getIsbn());
        verify(bookRepository, times(1)).existsById(anyString());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void addNewBook_BookAlreadyExists() {
        when(bookRepository.existsById(anyString())).thenReturn(true);

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            bookService.addNewBook(bookDto);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(bookRepository, times(1)).existsById(anyString());
        verify(bookRepository, times(0)).save(book);
    }

    @Test
    void addNewBook_AuthorNotFound() {
        when(bookRepository.existsById(anyString())).thenReturn(false);
        when(authorService.fetchAuthorById(anyLong())).thenReturn(Optional.empty());

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            bookService.addNewBook(bookDto);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(bookRepository, times(1)).existsById(anyString());
        verify(bookRepository, times(0)).save(book);
    }

    @Test
    void fetchBookByIsbn_Success() {
        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        BookDto result = bookService.fetchBookByIsbn("1234567890");

        assertNotNull(result);
        assertEquals(book.getIsbn(), result.getIsbn());
        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    void fetchBookByIsbn_BookNotFound() {
        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            bookService.fetchBookByIsbn("1234567890");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    void modifyBookByIsbn_Success() {
        when(bookRepository.existsById(anyString())).thenReturn(true);
        when(authorService.fetchAuthorById(anyLong())).thenReturn(Optional.of(author));
        when(bookRepository.save(book)).thenReturn(book);

        BookDto result = bookService.modifyBookByIsbn("1234567890", bookDto);

        assertNotNull(result);
        assertEquals(bookDto.getIsbn(), result.getIsbn());
        verify(bookRepository, times(1)).existsById(anyString());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void modifyBookByIsbn_BookNotFound() {
        when(bookRepository.existsById(anyString())).thenReturn(false);

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            bookService.modifyBookByIsbn("1234567890", bookDto);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(bookRepository, times(1)).existsById(anyString());
        verify(bookRepository, times(0)).save(book);
    }

    @Test
    void removeBookByIsbn_Success() {
        when(bookRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(bookRepository).deleteById(anyString());

        assertDoesNotThrow(() -> bookService.removeBookByIsbn("1234567890"));

        verify(bookRepository, times(1)).existsById(anyString());
        verify(bookRepository, times(1)).deleteById(anyString());
    }

    @Test
    void removeBookByIsbn_BookNotFound() {
        when(bookRepository.existsById(anyString())).thenReturn(false);

        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            bookService.removeBookByIsbn("1234567890");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(bookRepository, times(1)).existsById(anyString());
        verify(bookRepository, times(0)).deleteById(anyString());
    }

    @Test
    void getAllBooks_Success() {
        MultiValueMap<String, String> searchParams = new LinkedMultiValueMap<>();
        searchParams.add("isbn", "1234567890");
        Pageable pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(any(BooleanBuilder.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(book)));

        Page<BookDto> result = bookService.getAllBooks(searchParams, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository, times(1)).findAll(any(BooleanBuilder.class), any(Pageable.class));
    }

    @Test
    void validateParameters_InvalidParameter() {
        Set<String> actualParams = Set.of("invalid-param");
        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            bookService.validateParameters(actualParams);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void validateSortCriteria_InvalidSortOrder() {
        String invalidSortCriteria = "name,INVALID_ORDER";
        OnlineBookStoreException exception = assertThrows(OnlineBookStoreException.class, () -> {
            bookService.validateSortCriteria(invalidSortCriteria);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}