package com.example.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.example.demo.dao.BookRepository;
import com.example.demo.dto.BookDto;
import com.example.demo.entity.Author;
import com.example.demo.entity.Book;
import com.example.demo.entity.QBook;
import com.example.demo.exception.OnlineBookStoreException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import com.querydsl.core.BooleanBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public BookDto addNewBook(BookDto bookDto) {
        log.info("Entering addNewBook()");
        String isbn = bookDto.getIsbn();
        if (doesBookExist(isbn)) {
            throw new OnlineBookStoreException("Book with ISBN already exists", HttpStatus.CONFLICT);
        }

        Book book = convertFromDtoToBook(bookDto);
        Book savedBook = bookRepository.save(book);
        log.info("Leaving addNewBook()");
        return convertFromBookToDto(savedBook);
    }

    private boolean doesBookExist(String isbn) {
        log.info("Entering doesBookExist()");
        if (isbn == null || isbn.isEmpty() || isbn.isBlank()) {
            throw new OnlineBookStoreException("ISBN cannot be null or empty", HttpStatus.BAD_REQUEST);
        }

        log.info("Leaving doesBookExist()");
        return bookRepository.existsById(isbn);
    }

    Book convertFromDtoToBook(BookDto bookDto) {
        log.info("Entering convertFromDtoToBook()");
        Book book = new Book();
        book.setIsbn(bookDto.getIsbn());
        book.setTitle(bookDto.getTitle());
        Author author = authorService.fetchAuthorById(bookDto.getAuthorId())
                .orElseThrow(() -> new OnlineBookStoreException(
                        String.format("Author not found for the given ID: %s", bookDto.getAuthorId()),
                        HttpStatus.NOT_FOUND));
        book.setAuthor(author);
        log.info("Leaving convertFromDtoToBook()");
        return book;
    }

    @Cacheable(value = "book", key = "#isbn")
    public BookDto fetchBookByIsbn(String isbn) {
        log.info("Entering fetchBookByIsbn()");
        if (Objects.isNull(isbn) || isbn.isEmpty() || isbn.isBlank()) {
            throw new OnlineBookStoreException("ISBN cannot be null or empty", HttpStatus.BAD_REQUEST);
        }

        Book book = findBookByIsbn(isbn);
        log.info("Leaving fetchBookByIsbn()");
        return convertFromBookToDto(book);
    }

    public Book findBookByIsbn(String isbn) {
        log.info("Entering findBookByIsbn()");
        return bookRepository.findById(isbn).orElseThrow(
                () -> new OnlineBookStoreException("Book not found for the given ISBN", HttpStatus.NOT_FOUND));
    }

    @CachePut(value = "book", key = "#isbn")
    public BookDto modifyBookByIsbn(String isbn, BookDto bookDto) {
        log.info("Entering modifyBookByIsbn()");
        if (!doesBookExist(isbn)) {
            throw new OnlineBookStoreException(String.format("Book with the given ISBN - %s not found", isbn),
                    HttpStatus.NOT_FOUND);
        }

        Book book = convertFromDtoToBook(bookDto);
        book.setIsbn(isbn);
        Book updatedBookEntity = bookRepository.save(book);
        log.info("Leaving modifyBookByIsbn()");
        return convertFromBookToDto(updatedBookEntity);
    }

    private BookDto convertFromBookToDto(Book book) {
        log.info("Entering convertFromBookToDto");
        BookDto bookDto = new BookDto();
        bookDto.setIsbn(book.getIsbn());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthorId(book.getAuthor().getId());
        bookDto.setAuthorName(book.getAuthor().getName());
        log.info("Leaving convertFromBookToDto");
        return bookDto;
    }

    @CacheEvict(value = "book", key = "#isbn")
    public void removeBookByIsbn(String isbn) {
        log.info("Entering removeBookByIsbn()");
        if (!doesBookExist(isbn)) {
            throw new OnlineBookStoreException(String.format("Book with the given ISBN - %s not found", isbn),
                    HttpStatus.NOT_FOUND);
        }

        bookRepository.deleteById(isbn);
        log.info("Leaving removeBookByIsbn()");
    }

    public Page<BookDto> getAllBooks(MultiValueMap<String, String> searchOrFilterParameters, Pageable pageable) {
        log.info("Entering getAllBooks()");
        validateParameters(searchOrFilterParameters.keySet());
        validateSortCriteria(searchOrFilterParameters.getFirst("sort"));
        BooleanBuilder booleanBuilder = buildSearchOrFilterPredicate(searchOrFilterParameters);
        Page<Book> books = bookRepository.findAll(booleanBuilder, pageable);
        Page<BookDto> bookDtos = books.map(this::convertFromBookToDto);
        log.info("Leaving getAllBooks()");
        return bookDtos;
    }

    void validateParameters(Set<String> actualParameters) {
        log.info("Entering validateParameters()");
        Set<String> validParameters = Set.of("page-no", "page-size", "sort", "search", "author", "isbn", "title");
        List<String> invalidParameters =
                actualParameters.stream().filter(param -> !validParameters.contains(param)).toList();
        if (!invalidParameters.isEmpty()) {
            throw new OnlineBookStoreException(String.format("Unknown parameter(s) %s found", invalidParameters),
                    HttpStatus.BAD_REQUEST);
        }

        log.info("Leaving validateParameters()");
    }

    void validateSortCriteria(String sortCriteria) {
        log.info("Entering validateSortCriteria()");
        if (sortCriteria != null && !sortCriteria.isBlank()) {
            String[] sortSplit = sortCriteria.split(",", 2);
            if (sortSplit.length != 2) {
                throw new OnlineBookStoreException(
                        String.format("Invalid sort criteria '%s'. Should be something like 'name,ASC' or 'name,asc'",
                                sortCriteria),
                        HttpStatus.BAD_REQUEST);
            }

            String sortBy = sortSplit[0].trim();
            String sortOrder = sortSplit[1].trim().toLowerCase();
            if (!"asc".equalsIgnoreCase(sortOrder) && !"desc".equalsIgnoreCase(sortOrder)) {
                throw new OnlineBookStoreException(
                        String.format("Invalid sort-order [%s] for sort-by [%s]", sortOrder, sortBy),
                        HttpStatus.BAD_REQUEST);
            }

        }

        log.info("Leaving validateSortCriteria()");
    }

    private BooleanBuilder buildSearchOrFilterPredicate(MultiValueMap<String, String> searchOrFilterParameters) {
        log.info("Entering buildSearchOrFilterPredicate()");
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QBook book = QBook.book;

        String filterByIsbn = searchOrFilterParameters.getFirst("isbn");
        if (filterByIsbn != null && !filterByIsbn.isBlank()) {
            booleanBuilder.and(book.isbn.containsIgnoreCase(filterByIsbn));
        }

        String filterByAuthor = searchOrFilterParameters.getFirst("author");
        if (filterByAuthor != null && !filterByAuthor.isBlank()) {
            booleanBuilder.and(book.author.name.containsIgnoreCase(filterByAuthor));
        }

        String filterByTitle = searchOrFilterParameters.getFirst("title");
        if (filterByTitle != null && !filterByTitle.isBlank()) {
            booleanBuilder.and(book.title.containsIgnoreCase(filterByTitle));
        }

        log.info("Leaving buildSearchOrFilterPredicate()");
        return booleanBuilder;
    }

}
